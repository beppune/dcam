package org.dcam.model.core


class PolicyLexer(private val text:String) : Iterator<PolicyLexer.Token>, Iterable<PolicyLexer.Token>{
    enum class TokenType {
        WORD,

        UNKNOWN,
        LF,
        CR,
        WS,
        HT,
        DIGIT,
        COLON,
        PIPE,
        EQUAL,
        LT,
        GT,
        DQ,
        DOT
    }
    
    data class Token(val type: TokenType, var char:Char?=null)

    private var ptr:CharIterator = text.iterator()

    private fun giveToken(c: Char): Token = when (c) {

        '\n' -> Token(TokenType.LF)
        '\r' -> Token(TokenType.CR)
        '\t' -> Token(TokenType.HT)
        ' ' -> Token(TokenType.WS)
        ':' -> Token(TokenType.COLON)
        '|' -> Token(TokenType.PIPE)
        '=' -> Token(TokenType.EQUAL)
        '<' -> Token(TokenType.LT)
        '>' -> Token(TokenType.GT)
        '"' -> Token(TokenType.DQ)
        '.' -> Token(TokenType.DOT)

        else -> {

            if (c.isLetter()) Token(TokenType.WORD,c)
            else if (c.isDigit()) Token(TokenType.DIGIT,c)

            else Token(TokenType.UNKNOWN, c)
        }
    }

    override fun hasNext(): Boolean = ptr.hasNext()

    override fun next(): Token = giveToken(ptr.next())
    override fun iterator(): Iterator<Token> = this


}

class PolicyParser(text:String) {

    val lexer:PolicyLexer

    init {
        lexer = PolicyLexer(text)
    }

    fun parse() : PolicyManager {
        val p = PolicyManager()

        while (lexer.hasNext()) {
            var t = lexer.next()

        }

        return p
    }

}




