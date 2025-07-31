package use_case.review;

public class ReviewInputData {
    private final int recipeId;
    private final int rating;
    private final String author;
    private final String text;

    public ReviewInputData(int recipeId, int rating, String author, String text) {
        this.recipeId = recipeId;
        this.rating = rating;
        this.author = author;
        this.text = text;
    }

    public int getRecipeId() { return recipeId; }
    public int getRating() { return rating; }
    public String getAuthor() { return author; }
    public String getText() { return text; }
}
