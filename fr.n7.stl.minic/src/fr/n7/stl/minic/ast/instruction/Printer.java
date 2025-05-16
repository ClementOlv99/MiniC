/**
 * 
 */
package fr.n7.stl.minic.ast.instruction;

import fr.n7.stl.minic.ast.expression.Expression;
import fr.n7.stl.minic.ast.instruction.declaration.FunctionDeclaration;
import fr.n7.stl.minic.ast.scope.Declaration;
import fr.n7.stl.minic.ast.scope.HierarchicalScope;
import fr.n7.stl.minic.ast.type.ArrayType;
import fr.n7.stl.minic.ast.type.AtomicType;
import fr.n7.stl.minic.ast.type.NamedType;
import fr.n7.stl.minic.ast.type.Type;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.Library;
import fr.n7.stl.tam.ast.Register;
import fr.n7.stl.tam.ast.TAMFactory;

/**
 * Implementation of the Abstract Syntax Tree node for a printer instruction.
 * @author Marc Pantel
 *
 */
public class Printer implements Instruction {

	protected Expression parameter;

	public Printer(Expression _value) {
		this.parameter = _value;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "print " + this.parameter + ";\n";
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#collect(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean collectAndPartialResolve(HierarchicalScope<Declaration> _scope) {
		return parameter.collectAndPartialResolve(_scope);
	}
	
	@Override
	public boolean collectAndPartialResolve(HierarchicalScope<Declaration> _scope, FunctionDeclaration _container) {
		return this.collectAndPartialResolve(_scope);
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#resolve(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean completeResolve(HierarchicalScope<Declaration> _scope) {
		return parameter.completeResolve(_scope);
	}

/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Instruction#checkType()
	 */
	@Override
	public boolean checkType() {
		Type type = Filter(parameter.getType());
		return (type.compatibleWith((AtomicType.BooleanType))||type.compatibleWith((AtomicType.CharacterType))||type.compatibleWith((AtomicType.IntegerType)))||type.compatibleWith((AtomicType.StringType));
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Instruction#allocateMemory(fr.n7.stl.tam.ast.Register, int)
	 */
	@Override
	public int allocateMemory(Register _register, int _offset) {
		return 0;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Instruction#getCode(fr.n7.stl.tam.ast.TAMFactory)
	 */
	@Override
	public Fragment getCode(TAMFactory _factory) {
		Fragment fragment = _factory.createFragment();
		fragment.append(parameter.getCode(_factory));
		Type type = Filter(parameter.getType());
		fragment.addComment(this.toString());
		if (type.compatibleWith((AtomicType.BooleanType))) {
			fragment.add(Library.BOut);
		} else if (type.compatibleWith((AtomicType.StringType))) {
			for(int i=0;i<type.length();i++){
				fragment.add(Library.COut);
			}
		}else if (type.compatibleWith((AtomicType.CharacterType))) {
			fragment.add(Library.COut);
		} else {
			fragment.add(Library.IOut);
		}
		return fragment;
	}

		private Type Filter(Type typeOfValue){
		if(typeOfValue instanceof NamedType){
			NamedType type2 = (NamedType)typeOfValue;
			typeOfValue = type2.getType();
		} else if(typeOfValue instanceof ArrayType){
			ArrayType type2 = (ArrayType)typeOfValue;
			typeOfValue = type2.getType();
		}
		return typeOfValue;
	}

}
