package cz.brmlab.brmson.blanqa.framework.data;

public class NamedEntity {
	private String text;
	private float score;

	public NamedEntity(String text) {
		this.text = text;
		this.score = 0;
	}

	public String getText() {
		return text;
	}

	public float getScore() {
		return score;
	}
	public void setScore(float score) {
		this.score = score;
	}
	public void addScore(float score) {
		this.score += score;
	}
}
