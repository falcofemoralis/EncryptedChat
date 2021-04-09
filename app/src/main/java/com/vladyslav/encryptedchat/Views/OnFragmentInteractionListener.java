package com.vladyslav.encryptedchat.Views;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

public interface OnFragmentInteractionListener {

    /* Константы действий активити
     * NEXT_FRAGMENT_HIDE - Показать следующий фрагмент и спрятать текущий
     * NEXT_FRAGMENT_REPLACE - Показать следующий фрагмент, заменив текущий
     * RETURN_FRAGMENT_BY_TAG - Вернуться к фрагменту (состоянию) по тегу. Полезно в случае если необходимо сделать несколько popBackStack-ов
     * */
    enum Action {
        NEXT_FRAGMENT_HIDE,
        NEXT_FRAGMENT_NO_BACK_STACK,
        NEXT_FRAGMENT_REPLACE,
        RETURN_FRAGMENT_BY_TAG,
        POP_BACK_STACK
    }

    /**
     * Метод для общения между фрагментами
     * Параметры:
     * fragmentSource - фрагмент который вызвал метод
     * fragmentReceiver - Фрагмент с которым хотят взаимодействовать
     * data - данные, если они необходимы
     * action - одна из констант действий
     * backStackTag - тег для стека вызовов фрагментов
     */
    void onFragmentInteraction(Fragment fragmentSource, Fragment fragmentReceiver, Action action, Bundle data, String backStackTag);
}