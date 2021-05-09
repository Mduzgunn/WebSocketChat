package com.grad.prjct.util;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Component;

@Component
public class ActiveUserManager {

    private final Map<String, Object> map;

    private final List<ActiveUserChangeListener> listeners;

    private final ThreadPoolExecutor notifyPool;

    private ActiveUserManager() {
        map = new ConcurrentHashMap<>();
        listeners = new CopyOnWriteArrayList<>();
        notifyPool = new ThreadPoolExecutor(1, 5, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<>(100));
    }

    public void add(String userName, String remoteAddress) {
        map.put(userName, remoteAddress);
        notifyListeners();
    }

    /**
     * concurrentHashMap kullanarak sileriz
     * @param silinecek kullanıcı adı
     */
    public void remove(String username) {
        map.remove(username);
        notifyListeners();
    }

    /**
     * @return - aktif kullanıcılar
     */
    public Set<String> getAll() {
        return map.keySet();
    }

    /**
     * @return - alınan kullanıcı adı dışındaki kullanıcı isimleri
     */
    public Set<String> getActiveUsersExceptCurrentUser(String username) {
        Set<String> users = new HashSet<>(map.keySet());
        users.remove(username);
        return users;
    }
    
    /**
     * Aktif kullanıcılar değiştiğinde bildirim almak için
     *
     * @param listener
     */
    public void registerListener(ActiveUserChangeListener listener) {
        listeners.add(listener);
    }

    /**
     * disconnect eventi.Bütün bildirimlerin abonelikleri sonlandırma
     *
     * @param listener
     */
    public void removeListener(ActiveUserChangeListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        notifyPool.submit(() -> listeners.forEach(ActiveUserChangeListener::notifyActiveUserChange));
    }
}
