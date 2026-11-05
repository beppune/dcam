
use sqlx::{MySql, MySqlPool, Transaction, mysql::MySqlConnectOptions};
use tokio::runtime::{Builder, Runtime};

use crate::withactor::WithActor;


pub struct Backoffice {
    rt: Runtime,
    pool: MySqlPool,
}

impl Backoffice {

    pub fn new(options: MySqlConnectOptions) -> Self {

        let rt = Builder::new_current_thread()
            .enable_all().build().unwrap();

        let pool = {
            let _g = rt.enter();

            sqlx::MySqlPool::connect_lazy_with(options)
        };

        Self {
            rt,
            pool,
        }

    }

    pub fn run<F,T>(&self, f:F) -> Result<T, sqlx::Error>
        where 
            F: for<'a> AsyncFnOnce(&'a mut Transaction<'static,MySql>) -> Result<T, sqlx::Error>,
    {
        
        self.rt.block_on(async {
            let mut tx = self.pool.begin().await?;
            let res = f(&mut tx).await;

            match res {
                Ok(_) => tx.commit().await?,
                Err(_) => tx.rollback().await?,
            };

            res
        })
    }

    pub fn with(&self, actor: u32) -> WithActor<'_> {
        WithActor {
            bo: self,
            actor,
        }
    }
}

#[cfg(test)]
mod test {

use sqlx::{Error, mysql::MySqlConnectOptions};

use crate::backoffice::Backoffice;


    #[test]
    fn test() {
        let o = MySqlConnectOptions::new()
            .host("localhost")
            .username("root")
            .password("root")
            .database("gadc");
        let bo = Backoffice::new(o);
        
        let res = bo.run(async |tx| {
            let u:i32 = sqlx::query_scalar("SELECT 1")
                .fetch_one(&mut **tx).await?;
            Ok(u)
        });

        dbg!(&res);
        assert!(res.is_ok());
    }
}
