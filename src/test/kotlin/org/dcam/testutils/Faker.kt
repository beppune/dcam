package org.dcam.testutils

import net.datafaker.providers.base.AbstractProvider
import net.datafaker.providers.base.BaseFaker
import org.dcam.model.core.Request

class Faker() : AbstractProvider<BaseFaker>(BaseFaker()) {

    fun request() : Request {
        return Request()
    }

}