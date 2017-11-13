package foo.bar.service.utils;

public class HqlConditions {

	// Non value necessary
	public static final String IS_NULL = " is null ";
	public static final String IS_NOT_NULL = " is not null ";
	public static final String IS_EMPTY = " is empty ";
	public static final String IS_NOT_EMPTY = " is not empty ";

	// Field Value necessary
	public static final String LIKE = " like ";
	public static final String LIKE_IGNORE_CASE = " LIKE ";
	public static final String EQUALS = " = ";
	public static final String NOT_EQUALS = " <> ";
	public static final String GREATER_THAN = " > ";
	public static final String GREATER_EQUALS = " >= ";
	public static final String LOWER_THAN = " < ";
	public static final String LOWER_EQUALS = " <= ";

	// Range Values necessaries
	public static final String BETWEEN = " between ";
	
	// Reference field necessary or Field Value necessaries??
	public static final String IN = " in ";
	public static final String NOT_IN = " not in ";


	// TODO: posible cases to configure?
	public static final String MEMBER_OF = " member of ";
	public static final String NOT_MEMBER_OF = " not member of ";
	
	
}
