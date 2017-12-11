package foo.bar.filter;

public enum FilterAddCondition {

	AND(" and "),
	OR(" or ");

	private final String addCondition;

	@Override
	public String toString() {
		return this.addCondition;
	}

	FilterAddCondition(final String addCondition) {
		this.addCondition = addCondition;
	}
	
	public boolean equalsName(String otherName) {
        // (otherName == null) check is not needed because name.equals(null) returns false 
        return this.addCondition.equals(otherName);
    }
}
