package com.customwars.client.model.co;

/**
 * A commanding officer, subclasses should extend functions of interest
 */
public abstract class AbstractCO implements CO {
  private final String name;
  private final CoStyle style;
  private final String bio;
  private final String title;
  private final String hit;
  private final String miss;
  private final String skill;

  private final Power power;
  private final Power superpower;

  private final String[] intel;
  private final String[] defeat;
  private final String[] victory;
  private final String[] quotes;

  protected AbstractCO() {
    this("unnamed", CoStyle.NONE, "", "", "", "", "", new Power("", ""), new Power("", ""), null, null, null, null);
  }

  protected AbstractCO(String name, CoStyle style, String bio,
                       String title, String hit, String miss, String skill,
                       Power power, Power superPower,
                       String[] intel, String[] defeat, String[] victory, String[] quotes) {
    this.name = name;
    this.style = style;
    this.bio = bio;
    this.title = title;
    this.hit = hit;
    this.miss = miss;
    this.skill = skill;
    this.power = power;
    this.superpower = superPower;
    this.intel = intel;
    this.defeat = defeat;
    this.victory = victory;
    this.quotes = quotes;
  }

  /**
   * Copy Constructor
   *
   * @param co the co to copy
   */
  public AbstractCO(AbstractCO co) {
    this.name = co.name;
    this.style = co.style;
    this.bio = co.bio;
    this.title = co.title;
    this.hit = co.hit;
    this.miss = co.miss;
    this.skill = co.skill;
    this.power = new Power(co.power);
    this.superpower = new Power(co.superpower);
    this.intel = co.intel;
    this.defeat = co.defeat;
    this.victory = co.victory;
    this.quotes = co.quotes;
  }

  public String getName() {
    return name;
  }

  public CoStyle getStyle() {
    return style;
  }

  public String getBio() {
    return bio;
  }

  public String getTitle() {
    return title;
  }

  public String getHit() {
    return hit;
  }

  public String getMiss() {
    return miss;
  }

  public String getSkill() {
    return skill;
  }

  public Power getPower() {
    return power;
  }

  public Power getSuperpower() {
    return superpower;
  }

  public String[] getIntel() {
    return intel;
  }

  public String[] getQuotes() {
    return quotes;
  }

  public String[] getVictory() {
    return victory;
  }

  public String[] getDefeat() {
    return defeat;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof AbstractCO)) return false;

    AbstractCO that = (AbstractCO) o;

    if (!name.equals(that.name)) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }
}
