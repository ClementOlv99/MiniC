/**
 * 
 */
package fr.n7.stl.minic.ast.instruction.declaration;

import java.util.Iterator;
import java.util.List;

import org.antlr.v4.parse.ANTLRParser.blockEntry_return;

import fr.n7.stl.minic.ast.Block;
import fr.n7.stl.minic.ast.instruction.Instruction;
import fr.n7.stl.minic.ast.scope.Declaration;
import fr.n7.stl.minic.ast.scope.HierarchicalScope;
import fr.n7.stl.minic.ast.scope.SymbolTable;
import fr.n7.stl.minic.ast.type.Type;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.Library;
import fr.n7.stl.tam.ast.Register;
import fr.n7.stl.tam.ast.TAMFactory;
import fr.n7.stl.util.Logger;

/**
 * Abstract Syntax Tree node for a function declaration.
 * @author Marc Pantel
 */
public class FunctionDeclaration implements Instruction, Declaration {

	/**
	 * Name of the function
	 */
	protected String name;
	
	/**
	 * AST node for the returned type of the function
	 */
	protected Type type;
	
	/**
	 * List of AST nodes for the formal parameters of the function
	 */
	protected List<ParameterDeclaration> parameters;
	protected HierarchicalScope<Declaration> localScope;
	
	/**
	 * @return the parameters
	 */
	public List<ParameterDeclaration> getParameters() {
		return parameters;
	}

	/**
	 * AST node for the body of the function
	 */
	protected Block body;

	/**
	 * Builds an AST node for a function declaration
	 * @param _name : Name of the function
	 * @param _type : AST node for the returned type of the function
	 * @param _parameters : List of AST nodes for the formal parameters of the function
	 * @param _body : AST node for the body of the function
	 */
	public FunctionDeclaration(String _name, Type _type, List<ParameterDeclaration> _parameters, Block _body) {
		this.name = _name;
		this.type = _type;
		this.parameters = _parameters;
		this.body = _body;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String _result = this.type + " " + this.name + "( ";
		Iterator<ParameterDeclaration> _iter = this.parameters.iterator();
		if (_iter.hasNext()) {
			_result += _iter.next();
			while (_iter.hasNext()) {
				_result += " ," + _iter.next();
			}
		}
		return _result + " )" + this.body;
	}

	public String toStringSignature() {
		String _result = this.type + " " + this.name + "( ";
		Iterator<ParameterDeclaration> _iter = this.parameters.iterator();
		if (_iter.hasNext()) {
			_result += _iter.next();
			while (_iter.hasNext()) {
				_result += " ," + _iter.next();
			}
		}
		return _result + " )";
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Declaration#getName()
	 */
	@Override
	public String getName() {
		return this.name;
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Declaration#getType()
	 */
	@Override
	public Type getType() {
		return this.type;
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#collect(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean collectAndPartialResolve(HierarchicalScope<Declaration> _scope) {	
		boolean ok = true;
		if(_scope.accepts(this)) {
			_scope.register(this);
			ok = ok && true;
		} else {
			Logger.warning("Variable" + this.name + "Is already defined");
			return false;
		}	
		this.localScope = new SymbolTable(_scope);
		for(ParameterDeclaration p : parameters){
			if(this.localScope .accepts(p)) {
				this.localScope .register(p);
				ok = ok && true;
			} else {
				Logger.warning("Parameter " + p.getName() + "Is already defined");
				ok = ok && false;
			}
		}
		return ok && this.body.collectAndPartialResolve(localScope,this);
	}
	
	@Override
	public boolean collectAndPartialResolve(HierarchicalScope<Declaration> _scope, FunctionDeclaration _container) {
		boolean ok = true;
		if(_scope.accepts(this)) {
			_scope.register(this);
			ok = ok && true;
		} else {
			Logger.warning("Variable" + this.name + "Is already defined");
			return false;
		}	
		this.localScope = new SymbolTable(_scope);
		for(ParameterDeclaration p : parameters){
			if(this.localScope .accepts(p)) {
				this.localScope .register(p);
				ok = ok && true;
			} else {
				Logger.warning("Parameter " + p.getName() + "Is already defined");
				ok = ok && false;
			}
		}
		return ok && this.body.collectAndPartialResolve(localScope,this);

	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#resolve(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean completeResolve(HierarchicalScope<Declaration> _scope) {
		return (this.body.completeResolve(localScope) && this.type.completeResolve(localScope));
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#checkType()
	 */
	@Override
	public boolean checkType() {
		return body.checkType();
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#allocateMemory(fr.n7.stl.tam.ast.Register, int)
	 */
	@Override
	public int allocateMemory(Register _register, int _offset) {
		int paramSizes = 0;
		for(ParameterDeclaration p : parameters){
			p.offset =  paramSizes - p.getType().length();
			paramSizes = paramSizes - p.getType().length();
		}
		body.allocateMemory(Register.LB, _offset+3);
		return 0;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#getCode(fr.n7.stl.tam.ast.TAMFactory)
	 */
	@Override
	public Fragment getCode(TAMFactory _factory) {
		Fragment fragment = _factory.createFragment();
		fragment.add(_factory.createJump("SKIPFUNC"+this.name));
		fragment.addSuffix("FUNC"+this.name);
		fragment.append(body.getCode(_factory));
		fragment.addSuffix("SKIPFUNC"+this.name);
		return fragment;
	}

}
