package com.customwars.ui.menu;

import com.customwars.ai.BattleOptions;

public class MenuSession {
	private boolean isTitleScreen;
	private boolean isOptionsScreen;
	private boolean isChooseNewGameTypeScreen;
	private boolean isMapSelectScreen;
	private boolean isCOselectScreen;
	private boolean isSideSelect;
	private boolean isBattleOptionsScreen;
	private boolean isKeyMappingScreen;
	private boolean isSnailInfoScreen;
	private boolean altcostume;
	private boolean mainaltcostume;
	private int currentCursorXposition;
	private int currentCursorYposition;
	private int currentlyHighlightedItem;
	private int currentlyHighlightedItem2;
	private int[] coSelections;
	private int[] sideSelections;
	private boolean[] altSelections;
	private String filename;
	private int numOfArmiesOnMap;
	private int numCOs;
	private int selectedArmyAllegiance;
	private int[] propertyTypesOnSelectedMap;
	private int mapPage;
	private int currentMapCategory;
	private int currentlySelectedSubCategory;
	private boolean chooseKey;
	private boolean insertNewCO;
	private String[] usernames;
	private int glide;
	private int infono;
	private int skip;
	private int skipMax;
	private boolean isInfoScreen;
	private int backGlide;
	private BattleOptions bopt;
	private int day;
	private int turn;
	private int visibility;
	private String[] syslog;
	private String[] chatlog;
	private int syspos;
	private int chatpos;

	public MenuSession() {
		
		this.selectedArmyAllegiance = 0;
		this.propertyTypesOnSelectedMap = new int[] {0, 0, 0, 0, 0, 0};
		this.currentMapCategory = 0;
		this.currentlySelectedSubCategory = 0;
		this.chooseKey = false;
		this.insertNewCO = false;
		this.usernames = new String[] {"Unknown"};
		this.glide = -1;
		this.skip = 0;
		this.skipMax = 0;
		this.backGlide = -1;
		this.bopt = new BattleOptions();
		this.day = 1;
		this.turn = 1;
		this.visibility = 0;
		this.syslog = new String[] {"System Log"};
		this.chatlog = new String[] {"Chat Log"};
		this.syspos = 0;
		this.chatpos = 0;
	}

	public boolean isTitleScreen() {
		return isTitleScreen;
	}

	public void setIsTitleScreen(boolean isTitleScreen) {
		this.isTitleScreen = isTitleScreen;
	}

	public boolean isOptionsScreen() {
		return isOptionsScreen;
	}

	public void setIsOptionsScreen(boolean isOptionsScreen) {
		this.isOptionsScreen = isOptionsScreen;
	}

	public boolean isChooseNewGameTypeScreen() {
		return isChooseNewGameTypeScreen;
	}

	public void setIsChooseNewGameTypeScreen(boolean isChooseNewGameTypeScreen) {
		this.isChooseNewGameTypeScreen = isChooseNewGameTypeScreen;
	}

	public boolean isMapSelectScreen() {
		return isMapSelectScreen;
	}

	public void setIsMapSelectScreen(boolean isMapSelectScreen) {
		this.isMapSelectScreen = isMapSelectScreen;
	}

	public boolean isCOselectScreen() {
		return isCOselectScreen;
	}

	public void setIsCOselectScreen(boolean isCOselectScreen) {
		this.isCOselectScreen = isCOselectScreen;
	}

	public boolean isSideSelect() {
		return isSideSelect;
	}

	public void setSideSelect(boolean isSideSelect) {
		this.isSideSelect = isSideSelect;
	}

	public boolean isBattleOptionsScreen() {
		return isBattleOptionsScreen;
	}

	public void setIsBattleOptionsScreen(boolean isBattleOptionsScreen) {
		this.isBattleOptionsScreen = isBattleOptionsScreen;
	}

	public boolean isKeyMappingScreen() {
		return isKeyMappingScreen;
	}

	public void setIsKeymappingScreen(boolean isKeyMappingScreen) {
		this.isKeyMappingScreen = isKeyMappingScreen;
	}

	public boolean isSnailInfoScreen() {
		return isSnailInfoScreen;
	}

	public void setIsSnailInfoScreen(boolean isSnailInfoScreen) {
		this.isSnailInfoScreen = isSnailInfoScreen;
	}

	public boolean isAltcostume() {
		return altcostume;
	}

	public void setAltcostume(boolean altcostume) {
		this.altcostume = altcostume;
	}

	public boolean isMainaltcostume() {
		return mainaltcostume;
	}

	public void setMainaltcostume(boolean mainaltcostume) {
		this.mainaltcostume = mainaltcostume;
	}

	public int getCurrentCursorXposition() {
		return currentCursorXposition;
	}

	public void setCurrentCursorXposition(int currentCursorXposition) {
		this.currentCursorXposition = currentCursorXposition;
	}

	public int getCurrentCursorYposition() {
		return currentCursorYposition;
	}

	public void setCurrentCursorYposition(int currentCursorYposition) {
		this.currentCursorYposition = currentCursorYposition;
	}

	public int getCurrentlyHighlightedItem() {
		return currentlyHighlightedItem;
	}

	public void setCurrentlyHighlightedItem(int currentlyHighlightedItem) {
		this.currentlyHighlightedItem = currentlyHighlightedItem;
	}

	public int getCurrentlyHighlightedItem2() {
		return currentlyHighlightedItem2;
	}

	public void setCurrentlyHighlightedItem2(int currentlyHighlightedItem2) {
		this.currentlyHighlightedItem2 = currentlyHighlightedItem2;
	}

	public int[] getCoSelections() {
		return coSelections;
	}

	public void setCoSelections(int[] coSelections) {
		this.coSelections = coSelections;
	}

	public int[] getSideSelections() {
		return sideSelections;
	}

	public void setSideSelections(int[] sideSelections) {
		this.sideSelections = sideSelections;
	}

	public boolean[] getAltSelections() {
		return altSelections;
	}

	public void setAltSelections(boolean[] altSelections) {
		this.altSelections = altSelections;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public int getNumOfArmiesOnMap() {
		return numOfArmiesOnMap;
	}

	public void setNumOfArmiesOnMap(int numOfArmiesOnMap) {
		this.numOfArmiesOnMap = numOfArmiesOnMap;
	}

	public int getNumCOs() {
		return numCOs;
	}

	public void setNumCOs(int numCOs) {
		this.numCOs = numCOs;
	}

	public int getSelectedArmyAllegiance() {
		return selectedArmyAllegiance;
	}

	public void setSelectedArmyAllegiance(int selectedArmyAllegiance) {
		this.selectedArmyAllegiance = selectedArmyAllegiance;
	}

	public int[] getPropertyTypesOnSelectedMap() {
		return propertyTypesOnSelectedMap;
	}

	public void setPropertyTypesOnSelectedMap(int[] propertyTypesOnSelectedMap) {
		this.propertyTypesOnSelectedMap = propertyTypesOnSelectedMap;
	}

	public int getMapPage() {
		return mapPage;
	}

	public void setMapPage(int mapPage) {
		this.mapPage = mapPage;
	}

	public int getCurrentlySelectedMapCategory() {
		return currentMapCategory;
	}

	public void setCurrentlySelectedMapCategory(int currentMapCategory) {
		this.currentMapCategory = currentMapCategory;
	}

	public int getCurrentlySelectedSubCategory() {
		return currentlySelectedSubCategory;
	}

	public void setCurrentlySelectedSubCategory(int currentlySelectedSubCategory) {
		this.currentlySelectedSubCategory = currentlySelectedSubCategory;
	}

	public boolean isChooseKey() {
		return chooseKey;
	}

	public void setChooseKey(boolean chooseKey) {
		this.chooseKey = chooseKey;
	}

	public boolean isInsertNewCO() {
		return insertNewCO;
	}

	public void setInsertNewCO(boolean insertNewCO) {
		this.insertNewCO = insertNewCO;
	}

	public String[] getUsernames() {
		return usernames;
	}

	public void setUsernames(String[] usernames) {
		this.usernames = usernames;
	}

	public int getGlide() {
		return glide;
	}

	public void setGlide(int glide) {
		this.glide = glide;
	}

	public int getInfono() {
		return infono;
	}

	public void setInfono(int infono) {
		this.infono = infono;
	}

	public int getSkip() {
		return skip;
	}

	public void setSkip(int skip) {
		this.skip = skip;
	}

	public int getSkipMax() {
		return skipMax;
	}

	public void setSkipMax(int skipMax) {
		this.skipMax = skipMax;
	}

	public boolean isInfoScreen() {
		return isInfoScreen;
	}

	public void setIsInfoScreen(boolean isInfoScreen) {
		this.isInfoScreen = isInfoScreen;
	}

	public int getBackGlide() {
		return backGlide;
	}

	public void setBackGlide(int backGlide) {
		this.backGlide = backGlide;
	}

	public BattleOptions getBopt() {
		return bopt;
	}

	public void setBopt(BattleOptions bopt) {
		this.bopt = bopt;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public int getTurn() {
		return turn;
	}

	public void setTurn(int turn) {
		this.turn = turn;
	}

	public int getVisibility() {
		return visibility;
	}

	public void setVisibility(int visibility) {
		this.visibility = visibility;
	}

	public String[] getSyslog() {
		return syslog;
	}

	public void setSyslog(String[] syslog) {
		this.syslog = syslog;
	}

	public String[] getChatlog() {
		return chatlog;
	}

	public void setChatlog(String[] chatlog) {
		this.chatlog = chatlog;
	}

	public int getSyspos() {
		return syspos;
	}

	public void setSyspos(int syspos) {
		this.syspos = syspos;
	}

	public int getChatpos() {
		return chatpos;
	}

	public void setChatpos(int chatpos) {
		this.chatpos = chatpos;
	}

    public void changeToState(String s) {
    }
}