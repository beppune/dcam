use crate::backoffice::Backoffice;


pub struct WithActor<'a> {
    pub bo: &'a Backoffice,
    pub actor: u32,
}

#[derive(sqlx::FromRow)]
pub struct Person {
    id: u32,
    last_name: String,
    first_name: String,
    nationality: String,
    birth_date: String,
    cf: Option<String>,
}

impl WithActor<'_> {

    pub fn create_person(&self, p: &mut Person) -> Result<u32,sqlx::Error>{
        let sql = "CALL CreatePerson(?, ?, ?, ?, ?, @uc_person)";

        self.bo.run(async |tx| {

            // Eventually check permission and policies

            sqlx::query(sql)
                .bind(&p.last_name)
                .bind(&p.first_name)
                .bind(&p.nationality)
                .bind(&p.birth_date)
                .bind(&p.cf)
                .execute(&mut **tx).await?;

            let uc_iperson: u32 = sqlx::query_scalar("SELECT @uc_person")
                .fetch_one(&mut **tx).await?;

            p.id = uc_iperson;

            let audit_sql = "CALL CreateAuditEvent(?,?,?,?,?,?,?,?, UUID(), @audit_id)";

            sqlx::query(audit_sql)
                .bind("PERSON_CREATED")
                .bind("PERSON")
                .bind(uc_iperson.to_string())
                .bind(self.actor.to_string())
                .bind("test-runner")
                .bind("TECHNICAL")
                .bind("SUCCESS")
                .bind("Created a human application user during stored procedure testing")
                .execute(&mut **tx).await?;

            Ok(uc_iperson)

        })?;


        Ok(p.id)
    }

}

#[cfg(test)]
mod test {
    use sqlx::mysql::MySqlConnectOptions;

use crate::{backoffice::Backoffice, withactor::Person};


#[test]

    fn create_person() {
        let mut p = Person {
            id: 0,
            last_name: "Annio".to_string(),
            first_name: "Ennio".to_string(),
            nationality: "italian".to_uppercase().to_string(),
            birth_date: "1984-05-14".to_string(),
            cf: None,
        };

        let options = MySqlConnectOptions::new()
            .host("localhost")
            .username("root")
            .password("root")
            .database("gadc");

        let bo = Backoffice::new(options);
        let wa = bo.with(990005);

        let res = wa.create_person(&mut p);

        dbg!(&res);

        assert!(res.is_ok());
    }
}
