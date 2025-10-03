package app.model;

public class Author {
    private String id;
    private String name;
    private String affiliation;
    private Integer citations;
    private Integer hIndex;

    public Author() {}
    public Author(String id, String name, String affiliation, Integer citations, Integer hIndex) {
        this.id = id; this.name = name; this.affiliation = affiliation;
        this.citations = citations; this.hIndex = hIndex;
    }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAffiliation() { return affiliation; }
    public void setAffiliation(String affiliation) { this.affiliation = affiliation; }
    public Integer getCitations() { return citations; }
    public void setCitations(Integer citations) { this.citations = citations; }
    public Integer getHIndex() { return hIndex; }
    public void setHIndex(Integer hIndex) { this.hIndex = hIndex; }
}
