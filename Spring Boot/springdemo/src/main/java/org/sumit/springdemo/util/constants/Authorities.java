package org.sumit.springdemo.util.constants;


public enum Authorities {
    RESET_ANY_USER_PASSWORD(1l,"RESET_ANY_USER_PASSWORD"),
    ACCESS_ADMIN_PANEL(2l,"ACCESS_ADMIN_PANEL");
    private String authorityString;
    private Long authorityId;

    Authorities(Long authorityId, String AuthorityString) {
        this.authorityId = authorityId;
        this.authorityString = AuthorityString;
    }
    public String getAuthorityString() {
        return authorityString;
    }
    public Long getAuthorityId() {
        return authorityId;
    }
}
