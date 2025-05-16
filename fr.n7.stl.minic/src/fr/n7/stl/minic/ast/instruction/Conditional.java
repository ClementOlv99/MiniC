/**
 * 
 */
package fr.n7.stl.minic.ast.instruction;

import fr.n7.stl.minic.ast.Block;
import fr.n7.stl.minic.ast.expression.Expression;
import fr.n7.stl.minic.ast.instruction.declaration.FunctionDeclaration;
import fr.n7.stl.minic.ast.scope.Declaration;
import fr.n7.stl.minic.ast.scope.HierarchicalScope;
import fr.n7.stl.minic.ast.type.AtomicType;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.Register;
import fr.n7.stl.tam.ast.TAMFactory;

/**
 * Implementation of the Abstract Syntax Tree node for a conditional instruction.
 * @author Marc Pantel
 *
 */
public class Conditional implements Instruction {

	protected Expression condition;
	protected Block thenBranch;
	protected Block elseBranch;

	public Conditional(Expression _condition, Block _then, Block _else) {
		this.condition = _condition;
		this.thenBranch = _then;
		this.elseBranch = _else;
	}

	public Conditional(Expression _condition, Block _then) {
		this.condition = _condition;
		this.thenBranch = _then;
		this.elseBranch = null;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "if (" + this.condition + " )" + this.thenBranch + ((this.elseBranch != null)?(" else " + this.elseBranch):"");
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#collect(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean collectAndPartialResolve(HierarchicalScope<Declaration> _scope) {
		if (elseBranch == null) {
			return this.condition.collectAndPartialResolve(_scope) && this.thenBranch.collectAndPartialResolve(_scope);
		} else {
			return this.condition.collectAndPartialResolve(_scope) && this.thenBranch.collectAndPartialResolve(_scope) && this.elseBranch.collectAndPartialResolve(_scope);
		}
		
	
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#collect(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean collectAndPartialResolve(HierarchicalScope<Declaration> _scope, FunctionDeclaration _container) {
		if (elseBranch == null) {
			return this.condition.collectAndPartialResolve(_scope) && this.thenBranch.collectAndPartialResolve(_scope,_container);
		} else {
			return this.condition.collectAndPartialResolve(_scope) && this.thenBranch.collectAndPartialResolve(_scope,_container) && this.elseBranch.collectAndPartialResolve(_scope,_container);
		}
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#resolve(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean completeResolve(HierarchicalScope<Declaration> _scope) {
		if (elseBranch == null) {
			return this.condition.completeResolve(_scope) && this.thenBranch.completeResolve(_scope);
		} else {
			return this.condition.completeResolve(_scope) && this.thenBranch.completeResolve(_scope) && this.elseBranch.completeResolve(_scope);
		}
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Instruction#checkType()
	 */
	@Override
	public boolean checkType() {
		if (elseBranch == null) {
			return this.condition.getType().equalsTo(AtomicType.BooleanType) && this.thenBranch.checkType();
		} else {
			return this.condition.getType().equalsTo(AtomicType.BooleanType) && this.thenBranch.checkType() && this.elseBranch.checkType();
		}
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Instruction#allocateMemory(fr.n7.stl.tam.ast.Register, int)
	 */
	@Override
	public int allocateMemory(Register _register, int _offset) {

		if (elseBranch == null) {
			thenBranch.allocateMemory(_register, _offset);
			return 0;
		} else {
			thenBranch.allocateMemory(_register, _offset);
			elseBranch.allocateMemory(_register, _offset);
			return 0;
		}
		
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Instruction#getCode(fr.n7.stl.tam.ast.TAMFactory)
	 */
	@Override
	public Fragment getCode(TAMFactory _factory) {
		Fragment fragment = _factory.createFragment();
		int LabelNumber = _factory.createLabelNumber();
		if (elseBranch == null) {
			fragment.append(condition.getCode(_factory));
			fragment.add(_factory.createJumpIf("FIN_IF"+LabelNumber, 0));
			fragment.append(thenBranch.getCode(_factory));
			fragment.addSuffix("FIN_IF"+LabelNumber);
			
		} else {
			fragment.append(condition.getCode(_factory));
			fragment.add(_factory.createJumpIf("ELSE"+LabelNumber, 0));
			fragment.append(thenBranch.getCode(_factory));
			fragment.add(_factory.createJump("END_IF_ELSE"+LabelNumber));
			fragment.addSuffix("ELSE"+LabelNumber);
			fragment.append(elseBranch.getCode(_factory));
			fragment.addSuffix("END_IF_ELSE"+LabelNumber);
		}
		return fragment;
	}

}
