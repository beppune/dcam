package org.dcam.policy

import io.kotest.core.spec.style.AnnotationSpec
import org.dcam.model.core.Policy
import org.dcam.model.core.PolicyManager
import org.dcam.testutils.Faker

class TestParser : AnnotationSpec() {

    @Test
    fun shouldParseBook() {
        val text = """
            Book: Policy Book Example
            
            policy: No activity authorized between may 5 and may 10 included
            must not: request.from >= 10/5 and request.to <= 12/5
            
            policy: TLC and CARRIERS activities can be requested only from network department and only in workdays.
                    Requests in not working hours may be discussed
            must:       request.operator.dep == "NETWORK",
                        request.days.each : w | w.weekday != 6 and w.weekday != 7.
            
            may not:    request.timeframe : tf | tf.from < 8:00 or tf.to > 19:00
                  
        """.trimIndent()

        val exBook = PolicyManager.fromString(text)

        val request = Faker().request()

        /*Request
            .issuedBy("beppune")
            .from(LocalDate.of(2024, 5, 11))
            .to(LocalDate.of(2024, 5, 13))
            .at("TORINO")
            .into("TLC A", "TLC B")
            .todo("Switch configuration")
            .within(10.hours, 15.hours)
            .requesting()
                .visitor("FANTOZZI", "UGO", "cf23232", "CI", "AX33242", "21/06/2035")

         */
        val problems:List<Policy> = exBook.checkAll(request)

        problems.any { it.description.contains("No activity authorized between may 5 and may 10 included") }
    }

}