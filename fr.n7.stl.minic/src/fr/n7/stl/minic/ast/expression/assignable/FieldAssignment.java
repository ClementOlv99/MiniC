/**
 * 
 */
package fr.n7.stl.minic.ast.expression.assignable;

import fr.n7.stl.minic.ast.SemanticsUndefinedException;
import fr.n7.stl.minic.ast.expression.AbstractField;
import fr.n7.stl.minic.ast.expression.accessible.AccessibleExpression;
import fr.n7.stl.minic.ast.expression.accessible.BinaryOperator;
import fr.n7.stl.minic.ast.expression.accessible.FieldAccess;
import fr.n7.stl.minic.ast.instruction.declaration.VariableDeclaration;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.Library;
import fr.n7.stl.tam.ast.TAMFactory;
import fr.n7.stl.util.Logger;

/**
 * Abstract Syntax Tree node for an expression whose computation assigns a field in a record.
 * @author Marc Pantel
 *
 */
public class FieldAssignment extends AbstractField<AssignableExpression> implements AssignableExpression {

	/**
	 * Construction for the implementation of a record field assignment expression Abstract Syntax Tree node.
	 * @param _record Abstract Syntax Tree for the record part in a record field assignment expression.
	 * @param _name Name of the field in the record field assignment expression.
	 */
	public FieldAssignment(AssignableExpression _record, String _name) {
		super(_record, _name);
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.impl.FieldAccessImpl#getCode(fr.n7.stl.tam.ast.TAMFactory)
	 */
	@Override
	public Fragment getCode(TAMFactory _factory) {
		Fragment fragment = _factory.createFragment();

		//cas de field profondeur 0
		if(nwrecord instanceof VariableAssignment varec){
			VariableDeclaration varecd =  varec.declaration;
			
			fragment.add(_factory.createLoadA(varecd.getRegister(), varecd.getOffset()));
			fragment.addComment(this.toString());
			fragment.add(_factory.createLoadL(this.getField().getOffset()));
			fragment.add(TAMFactory.createBinaryOperator(BinaryOperator.Add));
			fragment.add(_factory.createStoreI(this.getField().getType().length()));
		}
		//cas des variables
		else if(nwrecord instanceof FieldAssignment frec){
			fragment.append(rec(_factory,frec));

			fragment.addComment(this.toString());
			fragment.add(_factory.createLoadL(this.getField().getOffset()));
			fragment.add(Library.IAdd);
			fragment.add(_factory.createStoreI(this.getField().getType().length()));

		}  
		//cas du flop
		else{
			Logger.error("L'assignement des structs ont flop Oh no");
		}
		return fragment;
	}

	private static Fragment rec(TAMFactory _factory,FieldAssignment fa) {
		Fragment fragment = _factory.createFragment();
		AssignableExpression nwrecord = fa.nwrecord;

		// Cas terminale
		if(nwrecord instanceof VariableAssignment vanwrec){
			VariableDeclaration varecd =  vanwrec.declaration;

			//cas des variables
			fragment.add(_factory.createLoadA(varecd.getRegister(), varecd.getOffset()));
			fragment.add(_factory.createLoadL(fa.getField().getOffset()));
			fragment.add(TAMFactory.createBinaryOperator(BinaryOperator.Add));

		}
		//cas recursif 
		else if(nwrecord instanceof FieldAssignment fnwrec){
			fragment.append(rec(_factory,fnwrec));
			fragment.add(_factory.createLoadL(fa.getField().getOffset()));
			fragment.add(Library.IAdd);

		} 
		//cas du flop
		else{
			Logger.error("L'assignement des structs ont flop Oh no");
		}
		return fragment;
	}
	
}
