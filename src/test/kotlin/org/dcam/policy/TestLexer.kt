package org.dcam.policy

import io.kotest.core.spec.style.AnnotationSpec
import org.dcam.model.core.PolicyLexer

class TestLexer : AnnotationSpec() {

    @Test
    fun lex() {
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

        val lexer = PolicyLexer(text)

        for( t in lexer ) {
            println("Type: ${t.type}. Value: ${t.char}.")
        }

    }

}