package engine;

public interface GameListener {
	void onFoodChanged(double food);
	void onTreasuryChanged(double treasury);
	void onTurnCountChanged(int turn);
	void onSiegeLimitReached(City city);
	void onCityReached(City city);
}
