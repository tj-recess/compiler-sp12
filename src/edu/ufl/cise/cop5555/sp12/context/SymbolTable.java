package edu.ufl.cise.cop5555.sp12.context;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import edu.ufl.cise.cop5555.sp12.ast.Declaration;

public class SymbolTable
{
    private class SymbolTableEntry
    {
        private int scope;
        private Declaration declaration;
        
        public SymbolTableEntry(int scope, Declaration declaration)
        {
            this.scope = scope;
            this.declaration = declaration;
        }
        
        public int getScope()
        {
            return scope;
        }

        public Declaration getDeclaration()
        {
            return declaration;
        }
    }
    
    private Stack<Integer> scopeStack;
    private int currentScope = 0;   //important to keep it 0
    private HashMap<String, List<SymbolTableEntry>> symbolTable;
    
	public SymbolTable()
	{
	    this.scopeStack = new Stack<Integer>();
	    this.symbolTable = new HashMap<String, List<SymbolTableEntry>>();
	}


	public void enterScope()
	{
		this.scopeStack.push(currentScope++);
	}

	public void exitScope()
	{
	    this.scopeStack.pop();
	}

	/**
	 * returns the in-scope declaration of the name if there is one,
	 * otherwise it returns null
	 * @param ident identifier to be looked up
	 * @return Declaration corresponding to the identifier
	 */
	public Declaration lookup(String ident) 
	{
	    List<SymbolTableEntry> matchingList = this.symbolTable.get(ident);
	    if(matchingList == null)
	        return null;
	    
        SymbolTableEntry bestEntry = null;	    
	    for(int i = scopeStack.size() - 1; i >= 0; i--)    //start from the top to bottom
        {
	        int bestScope = scopeStack.get(i);
	        for(SymbolTableEntry anEntry : matchingList)
	        {
	            if(anEntry.getScope() == bestScope && bestEntry == null)
	            {
	                bestEntry = anEntry;
	                return bestEntry.getDeclaration();  //found the best entry, there is no other possibility
	            }
	        }
	    }
	    return null;
	}

	/**
	 * If the name is already declared IN THE CURRENT SCOPE, returns false.
	 * Otherwise inserts the declaration in the symbol table
	 * @return false If the name is already declared in the CURRENT SCOPE
	 */
	public boolean insert(String ident, Declaration dec) 
	{
	    List<SymbolTableEntry> existingList = this.symbolTable.get(ident);
	    if(existingList == null)
	    {
	        existingList = new LinkedList<SymbolTableEntry>();
	        this.symbolTable.put(ident, existingList);
	    }
	    else
	    {
	        for(SymbolTableEntry anEntry : existingList)
	        {
	            if(anEntry.getScope() == scopeStack.peek())
	            {
	                return false;
	            }
	        }
	    }
	    //always insert at 0th index, avoid going to the end of list 
	    //(just in case "tail" pointer isn't maintained
	    existingList.add(0, new SymbolTableEntry(scopeStack.peek(), dec));
	    return true;
	}


    public int getCurrentScope()
    {
        return this.scopeStack.peek();
    }


}