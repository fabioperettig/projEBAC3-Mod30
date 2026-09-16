package br.com.fabioperettig.dao.generic;

import java.util.HashMap;
import java.util.Map;

/// Classe Singleton que garante que o MAP será único em toda a vita da aplicação.
public class SingletonMap {

    private static SingletonMap singletonMap;

    protected Map<Class, Map<?, ?>> map;

    private SingletonMap() {
        map = new HashMap<>();
    }

    /// Padrão com *syncronized e double checked* para garantir uma única THREAD
    public static SingletonMap getInstance() {
        if (singletonMap == null) {
            synchronized (SingletonMap.class){
                if (singletonMap == null) {
                singletonMap = new SingletonMap();
                }
            }
        }
        return singletonMap;
    }

    public Map<Class, Map<?, ?>> getMap() {
        return this.map;
    }
}
