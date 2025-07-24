package entity;

/**
 * recipe object. potential fields:
 * id, title/name, ingredients, steps/instructions,
 * imageurl, nutrition, authorId, isPublic (?)
 */
public class Recipe {
    private String id;
    private String name;
    private List<String> ingredients;
    private String steps;
    private String authorId;

    public Recipe(String id, String name, List<String> ingredients, String steps, String authorId) {
        this.id = id;
        this.name = name;
        this.ingredients = ingredients;
        this.steps = steps;
        this.authorId = authorId;
    }

    public String getId() {return id;}
    public void setId(String id) {this.id = id;}

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public List<String> getIngredients() {return ingredients;}
    public void setIngredients(List<String> ingredients) {this.ingredients = ingredients;}

    public String getSteps() {return steps;}
    public void setSteps(String steps) {this.steps = steps;}

    public String getAuthorId() {return authorId;}
    public void setAuthorId(String authorId) {this.authorId = authorId;}


}
