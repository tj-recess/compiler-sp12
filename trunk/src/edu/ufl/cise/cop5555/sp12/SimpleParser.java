package edu.ufl.cise.cop5555.sp12;

import edu.ufl.cise.cop5555.sp12.TokenStream;


	public class SimpleParser {

		TokenStream stream;
		
		public SimpleParser(TokenStream stream)
		{
		}
		
		public SyntaxException parse() {
		try{
			Program();  //method corresponding to start symbol
			return null;
		}   catch (SyntaxException e){return e;}
		}

		private void Program() throws SyntaxException{
			// TODO IMPLEMENT THIS		
		}

	    //IMPLEMENT OTHER METHODS


	}
	