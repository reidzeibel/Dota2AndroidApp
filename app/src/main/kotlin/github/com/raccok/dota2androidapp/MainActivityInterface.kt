package github.com.raccok.dota2androidapp

/**
 * Created by RidwanAditama on 17/09/2017.
 *
 * Interface to bridge the interaction between MainBackend class and MainActivity class
 * Since we want to separate the logic class and the view class, this class will help
 * sending the information from logic to the view.
 *
 * There are lots of options to implement this kind of function, this is just an example
 *
 */
interface MainActivityInterface {

    fun onValidInput(userInput : String)
    fun onInvalidInput()
}