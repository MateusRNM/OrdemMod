package com.mateus.ordemmod.Data;

import java.util.Map;

public class PlayerData implements IPlayerData{

    Map<String, Integer> Attributes;
    int NEX;
    String Classe;
    @Override
    public Map<String, Integer> getAttributes() {
        return this.Attributes;
    }

    @Override
    public void setAttributes(String attributeName, int attributeValue) {
        this.Attributes.put(attributeName, attributeValue);
    }

    @Override
    public int getNex() {
        return this.NEX;
    }

    @Override
    public void setNex(int nex) {
        this.NEX = nex;
    }

    @Override
    public String getClasse() {
        return this.Classe;
    }

    @Override
    public void setClasse(String classe) {
        this.Classe = classe;
    }
}
