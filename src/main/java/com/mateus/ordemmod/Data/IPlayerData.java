package com.mateus.ordemmod.Data;

import java.util.Map;

public interface IPlayerData {
    Map<String, Integer> getAttributes();
    void setAttributes(String attributeName, int attributeValue);
    int getNex();
    void setNex(int nex);
    String getClasse();
    void setClasse(String classe);
}
