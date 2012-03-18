package edu.ufl.cise.cop5555.sp12;

import static edu.ufl.cise.cop5555.sp12.Kind.EOF;

import java.io.IOException;
import java.util.HashMap;

import org.junit.Assert;

import edu.ufl.cise.cop5555.sp12.TokenStream.Token;

public class Scanner
{

    /**
     * local reference to TokenStream object for convenience
     */
    private TokenStream stream;

    /**
     * current state identified during the scan of input stream
     */
    private State state;

    /**
     * points to next char to process during scanning or none if past the end of
     * array
     */
    private int index;

    /**
     * stores mapping of string word to Kind
     */
    private HashMap<String, Kind> keywordMap = new HashMap<String, Kind>();

    private enum State
    {
        START, GOT_EQUALS, INDENT_START, IDENT_PART, KEYWORD, LITERAL, SEPARATOR, OPERATOR, WHITE_SPACE, COMMENT_START, INTEGER_LITERAL, STRING_LITERAL, BOOLEAN_LITERAL, STRING_CHARACTER, ESCAPE_SEQUENCE, GOT_ZERO, DIGITS, EOF, GOT_LESS_THAN, GOT_GREATER_THAN, ILLEGAL_CHAR, GOT_NOT, GOT_HASH, GOT_HASH_INSIDE_COMMENT, GOT_QUOTE, GOT_BACKSLASH
    }

    public Scanner(TokenStream stream)
    {
        this.stream = stream;
        this.index = 0;

        keywordMap.put("prog", Kind.PROG);
        keywordMap.put("gorp", Kind.GORP);
        keywordMap.put("string", Kind.STRING);
        keywordMap.put("int", Kind.INT);
        keywordMap.put("boolean", Kind.BOOLEAN);
        keywordMap.put("map", Kind.MAP);
        keywordMap.put("if", Kind.IF);
        keywordMap.put("else", Kind.ELSE);
        keywordMap.put("fi", Kind.FI);
        keywordMap.put("do", Kind.DO);
        keywordMap.put("od", Kind.OD);
        keywordMap.put("print", Kind.PRINT);
        keywordMap.put("println", Kind.PRINTLN);
    }

    public void scan()
    {
        Token aToken = null;
        do
        {
            try
            {
                aToken = next();
            } catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            stream.tokens.add(aToken);
        } while (!aToken.kind.equals(EOF));
    }

    /**
     * get next character from token stream and update index
     */
    private char getch() throws IOException
    {
        if (index < stream.inputChars.length)
        {
            return this.stream.inputChars[index++];
        }
        return 26;
    }

    private Token next() throws IOException
    {
        this.state = State.START;
        Token t = null;
        int beginOffset = index;
        char ch = getch();
        do
        {
            switch (this.state)
            {
            case START:
                // check next character and set state
                switch (ch)
                {
                case 26:
                    t = stream.new Token(Kind.EOF, beginOffset, index);
                    break;
                case ' ':
                case '\t':
                case '\n':
                case '\r':
                case '\f': // white-space
                    // reset the beginOffset to index
                    beginOffset = index;
                    break;
                case '=':
                    state = State.GOT_EQUALS;
                    break;
                case '<':
                    state = State.GOT_LESS_THAN;
                    break;
                case '>':
                    state = State.GOT_GREATER_THAN;
                    break;
                case '!':
                    state = State.GOT_NOT;
                    break;
                case '*':
                    t = stream.new Token(Kind.TIMES, beginOffset, index);
                    break;
                case '+':
                    t = stream.new Token(Kind.PLUS, beginOffset, index);
                    break;
                case '-':
                    t = stream.new Token(Kind.MINUS, beginOffset, index);
                    break;
                case '/':
                    t = stream.new Token(Kind.DIVIDE, beginOffset, index);
                    break;
                case '&':
                    t = stream.new Token(Kind.AND, beginOffset, index);
                    break;
                case '|':
                    t = stream.new Token(Kind.OR, beginOffset, index);
                    break;
                case '0':
                    t = stream.new Token(Kind.INTEGER_LITERAL, beginOffset,
                            index);
                    break;

                // separators
                case '.':
                    t = stream.new Token(Kind.DOT, beginOffset, index);
                    break;

                case ';':
                    t = stream.new Token(Kind.SEMI, beginOffset, index);
                    break;

                case ',':
                    t = stream.new Token(Kind.COMMA, beginOffset, index);
                    break;

                case '(':
                    t = stream.new Token(Kind.LEFT_PAREN, beginOffset, index);
                    break;

                case ')':
                    t = stream.new Token(Kind.RIGHT_PAREN, beginOffset, index);
                    break;

                case '[':
                    t = stream.new Token(Kind.LEFT_SQUARE, beginOffset, index);
                    break;

                case ']':
                    t = stream.new Token(Kind.RIGHT_SQUARE, beginOffset, index);
                    break;

                case ':':
                    t = stream.new Token(Kind.COLON, beginOffset, index);
                    break;

                case '{':
                    t = stream.new Token(Kind.LEFT_BRACE, beginOffset, index);
                    break;

                case '}':
                    t = stream.new Token(Kind.RIGHT_BRACE, beginOffset, index);
                    break;

                // comment

                case '#':
                    state = State.GOT_HASH;
                    break;

                // string

                case '"':
                    state = State.GOT_QUOTE;
                    break;

                default:
                    if (Character.isDigit(ch))
                    {
                        state = State.DIGITS;
                    } else if (Character.isJavaIdentifierStart(ch))
                    {
                        state = State.IDENT_PART;
                    } else
                    {
                        t = stream.new Token(Kind.ILLEGAL_CHAR, beginOffset,
                                index);
                    }
                }
                break; // end of START state

            case GOT_EQUALS:
                if (ch == '=')
                {
                    t = stream.new Token(Kind.EQUALS, beginOffset, index);
                } else
                {
                    moveIndexBack(ch);
                    t = stream.new Token(Kind.ASSIGN, beginOffset, index);
                }
                break;

            case GOT_LESS_THAN:
                if (ch == '=')
                {
                    t = stream.new Token(Kind.AT_MOST, beginOffset, index);
                } else
                {
                    moveIndexBack(ch);
                    t = stream.new Token(Kind.LESS_THAN, beginOffset, index);
                }
                break;

            case GOT_GREATER_THAN:
                if (ch == '=')
                {
                    t = stream.new Token(Kind.AT_LEAST, beginOffset, index);
                } else
                {
                    moveIndexBack(ch);
                    t = stream.new Token(Kind.GREATER_THAN, beginOffset, index);
                }
                break;

            case GOT_NOT:
                if (ch == '=')
                {
                    t = stream.new Token(Kind.NOT_EQUALS, beginOffset, index);
                } else
                {
                    moveIndexBack(ch);
                    t = stream.new Token(Kind.NOT, beginOffset, index);
                }
                break;

            case DIGITS:
                if (!Character.isDigit(ch))
                {
                    moveIndexBack(ch);
                    t = stream.new Token(Kind.INTEGER_LITERAL, beginOffset,
                            index);
                }
                break;

            case IDENT_PART:
                if (!Character.isJavaIdentifierPart(ch) || ch == 26) // EOF = 26
                {
                    moveIndexBack(ch);
                    if (isBooleanIdentifier(beginOffset, index))
                    {
                        t = stream.new Token(Kind.BOOLEAN_LITERAL, beginOffset,
                                index);
                    } else
                    {
                        Kind keywordType = getKeyword(beginOffset, index);
                        if (keywordType != null)
                        {
                            t = stream.new Token(keywordType, beginOffset,
                                    index);
                        } else
                        {
                            t = stream.new Token(Kind.IDENTIFIER, beginOffset,
                                    index);
                        }
                    }
                }
                break;

            case GOT_HASH:
                if (ch == '#')
                {
                    state = State.COMMENT_START;
                } else
                {
                    moveIndexBack(ch);
                    t = stream.new Token(Kind.MALFORMED_COMMENT, beginOffset,
                            index);
                }
                break;

            case COMMENT_START:
                if (ch == '#')
                {
                    state = State.GOT_HASH_INSIDE_COMMENT;
                } else if (ch == 26)
                {
                    t = stream.new Token(Kind.MALFORMED_COMMENT, beginOffset,
                            index);
                }
                break;

            case GOT_HASH_INSIDE_COMMENT:
                if (ch == '#') // complete comment
                {
                    // no need to make a token, just return to START state
                    state = State.START;
                    beginOffset = index;
                } else if (ch == 26) // EOF
                {
                    t = stream.new Token(Kind.MALFORMED_COMMENT, beginOffset,
                            index);
                } else
                {
                    state = State.COMMENT_START;
                }
                break;

            case GOT_QUOTE:
                switch (ch)
                {
                case '\\':
                    state = State.GOT_BACKSLASH;
                    break;
                case '"':
                    t = stream.new Token(Kind.STRING_LITERAL, beginOffset,
                            index);
                    break;
                case '\r':
                case '\n':
                case 26: // 26 = EOF
                    t = stream.new Token(Kind.MALFORMED_STRING, beginOffset,
                            index);
                    break;
                }
                break;

            case GOT_BACKSLASH:
                switch (ch)
                {
                case 't':
                case 'n':
                case 'f':
                case 'r':
                case '"':
                case '\\':
                    state = State.GOT_QUOTE;
                    break;
                default:
                    t = stream.new Token(Kind.MALFORMED_STRING, beginOffset,
                            index);
                }
                break;

            default:
                Assert.assertFalse("should not reach here", true);
            }

            if (t == null)
            {
                ch = getch();
            }

        } while (t == null);

        return t;
    }

    /**
     * Finds out whether a given token is a keyword in this language or not
     * 
     * @param begin
     *            index in the array from where token should be read
     * @param end
     *            index in array until where token should be read
     * @return keywordType which will store the value of keyword in terms of
     *         Kind enumeration
     */
    private Kind getKeyword(int begin, int end)
    {
        Kind keywordType = this.keywordMap.get(getStringFromInputChars(begin,
                end, stream.inputChars));
        return keywordType;
    }

    /**
     * ad hoc method to convert some part of char[] array into string
     * 
     * @param begin
     *            starting index in array
     * @param end
     *            value next to last index of string in array
     * @param array
     *            from which chars are chosen
     * @return String formed by above 3 parameters
     */
    private String getStringFromInputChars(int begin, int end, char[] array)
    {
        StringBuilder sb = new StringBuilder();
        for (; begin < end; begin++)
        {
            sb.append(array[begin]);
        }
        return sb.toString();
    }

    /**
     * returns true if word represented by input character array is a boolean
     * literal (true/false)
     * 
     * @param begin
     *            location from where word should be read
     * @param end
     *            location until where the word should be read
     * @return true if word is a boolean literal
     */
    private boolean isBooleanIdentifier(int begin, int end)
    {
        String actualWord = getStringFromInputChars(begin, end,
                stream.inputChars);
        if (actualWord.equals("true") || actualWord.equals("false"))
        {
            return true;
        }
        return false;
    }

    /**
     * This method is called when we read an extra character and need to move
     * index back by 1 pointer.
     */
    private void moveIndexBack(char nextChar)
    {
        if (nextChar != 26)
        {
            this.index--;
        }
    }
}
