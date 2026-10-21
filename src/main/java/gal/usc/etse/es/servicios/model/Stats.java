package gal.usc.etse.es.servicios.model;

public class Stats {
    int hp;
    int attack;
    int defense;
    int special_attack;
    int special_defense;
    int speed;

    public Stats() {}

    public int getHp() { return hp; }
    public void setHp(int hp) { this.hp = hp; }

    public int getAtk() { return attack; }
    public void setAtk(int atk) { this.attack = atk; }

    public int getDef() { return defense; }
    public void setDef(int def) { this.defense = def; }

    public int getSpa() { return special_attack; }
    public void setSpa(int spa) { this.special_attack = spa; }

    public int getSpd() { return special_defense; }
    public void setSpd(int spd) { this.special_defense = spd; }

    public int getSpe() { return speed; }
    public void setSpe(int spe) { this.speed = spe; }
}
