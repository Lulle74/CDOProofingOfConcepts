/**
 */
package library;

import org.eclipse.emf.cdo.CDOObject;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Library</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link library.Library#getListAuthor <em>List Author</em>}</li>
 *   <li>{@link library.Library#getListBook <em>List Book</em>}</li>
 * </ul>
 *
 * @see library.LibraryPackage#getLibrary()
 * @model
 * @extends CDOObject
 * @generated
 */
public interface Library extends CDOObject
{
	/**
	 * Returns the value of the '<em><b>List Author</b></em>' containment reference list.
	 * The list contents are of type {@link library.Author}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>List Author</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>List Author</em>' containment reference list.
	 * @see library.LibraryPackage#getLibrary_ListAuthor()
	 * @model containment="true"
	 * @generated
	 */
	EList<Author> getListAuthor();

	/**
	 * Returns the value of the '<em><b>List Book</b></em>' containment reference list.
	 * The list contents are of type {@link library.Book}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>List Book</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>List Book</em>' containment reference list.
	 * @see library.LibraryPackage#getLibrary_ListBook()
	 * @model containment="true"
	 * @generated
	 */
	EList<Book> getListBook();

} // Library
