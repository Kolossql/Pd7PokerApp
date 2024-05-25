package com.example.pokerappreal

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.pokerappreal.databinding.GameBinding
import com.example.pokerappreal.databinding.TitleBinding
import java.util.*
import android.widget.Toast
import java.lang.Math.random

class Game : Fragment() {

    private var _binding: GameBinding? = null
    private var totalBet = 0
    private var purse: Int = 1000
    private var centerCards: MutableList<String> = mutableListOf()
    private var handCards: MutableList<String> = mutableListOf()
    private var turn = 0
    private var deck = (0..51).toMutableList()
    private var AIhands: MutableList<List<String>> = mutableListOf()
    private var activeAIs: MutableList<Boolean> = mutableListOf(true,true,true,true,true,true)
    private val playerFolded = false


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @SuppressLint("ResourceAsColor")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = GameBinding.inflate(inflater, container, false)
        binding.root.setBackgroundColor(R.color.bg)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        if (arguments?.getInt("numOpponents") != null) {
            for (i in 0 until requireArguments().getInt("numOpponents") + 1){
                val hand: MutableList<String> = mutableListOf()
                hand.add(pickCard())
                hand.add(pickCard())
                AIhands.add(hand)
            }
        }else{
            for (i in 0 until 4){
                val hand: MutableList<String> = mutableListOf()
                hand.add(pickCard())
                hand.add(pickCard())
                AIhands.add(hand)
            }
        }
        var card: String = pickCard()
        handCards.add(card)
        val context = requireContext()
        var imgID = resources.getIdentifier(card, "drawable", context.packageName)
        binding.HandCard1.setImageResource(imgID)
        card = pickCard()
        handCards.add(card)
        imgID = resources.getIdentifier(card, "drawable", context.packageName)
        binding.HandCard2.setImageResource(imgID)
        binding.Check.setOnClickListener {
            turn++
            for (i in 0 until AIhands.size){
                if (activeAIs[i] == true) {
                    Toast.makeText(context, "Player ${i + 1}, Checks", Toast.LENGTH_SHORT).show()
                }
            }
            if (turn == 1) {
                flop()
            }else if (turn >= 4){
                end()
            }
            else{
                reveal()
            }




        }
        binding.Raise.setOnClickListener {
            totalBet += AIturns()
            purse -= 100
            totalBet += 100
            if (checkEnd()){
                purse+=totalBet
                reset()
            }
            binding.Purse.text = "Total Purse: $purse"
            binding.TotalBet.text = "Total Bet: $totalBet"
        }
        binding.Bet.setOnClickListener {
            totalBet += AIturns()
            purse -= 100
            totalBet += 100
            if (checkEnd()){
                purse+=totalBet
                reset()
            }
            binding.Purse.text = "Total Purse: $purse"
            binding.TotalBet.text = "Total Bet: $totalBet"


        }
        binding.Fold.setOnClickListener {
            end()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    fun AIturns(): Int{
        var totalBet = 0
        for (i in 0 until AIhands.size){
            if (!activeAIs[i]) continue
            val cardGroup = AIhands[i].toMutableList()
            cardGroup.addAll(centerCards)
            val AIValue: Double =  (determineHand(cardGroup).toDouble() + 800 - turn*50 - totalBet*.5)/1000
            Log.d("AIValue", AIValue.toString())
            if (random()<AIValue && determineHand(cardGroup) < 400){
                totalBet += 100
                Toast.makeText(context, "Player ${i+1}, Matches +$100",Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context, "Player ${i+1}, Folds",Toast.LENGTH_SHORT).show()
                activeAIs[i] = false
            }
        }
        return totalBet
    }
    fun end() {
        var max = 0
        var maxNum = 0
        for (i in 0 until AIhands.size) {
            if (activeAIs[i] == true) {
                val cardGroup = AIhands[i].toMutableList()
                cardGroup.addAll(centerCards)
                if (determineHand(cardGroup) > max) {
                    max = determineHand(cardGroup)
                    maxNum = i
                }
            }
        }
        val cardGroup = handCards.toMutableList()
        cardGroup.addAll(centerCards)
        if (max>determineHand(cardGroup)){
            Toast.makeText(context, "Player ${maxNum+1}, Wins $totalBet",Toast.LENGTH_SHORT).show()
        }else{
            purse+= totalBet
            Toast.makeText(context, "You Win $totalBet",Toast.LENGTH_SHORT).show()
        }

        reset()
    }
    fun reveal(){

        val context = requireContext()
        val card: String = pickCard()
        centerCards.add(card)
        val imgID = resources.getIdentifier(card, "drawable", context.packageName)
        if (turn == 2) {
            binding.CenterCard4.setImageResource(imgID)
        }else if (turn == 3){
            binding.CenterCard5.setImageResource(imgID)
        }

    }
    fun flop(){

        val context = requireContext()

        var card: String = pickCard()
        var imgID = resources.getIdentifier(card, "drawable", context.packageName)
        binding.CenterCard1.setImageResource(imgID)
        centerCards.add(card)
        card = pickCard()
        imgID = resources.getIdentifier(card, "drawable", context.packageName)
        binding.CenterCard2.setImageResource(imgID)
        centerCards.add(card)

        card = pickCard()
        imgID = resources.getIdentifier(card, "drawable", context.packageName)
        binding.CenterCard3.setImageResource(imgID)
        centerCards.add(card)

    }
    fun pickCard(): String{
        val randnum = deck.random()
        deck.remove(randnum)
        val cardnum = randnum%13+2

        val cardnumS = when(cardnum){
            11 -> "j"
            12 -> "q"
            13 -> "k"
            14 -> "a"
            else -> cardnum.toString()
        }
        val suit = when(randnum/13){
            0 -> "spades"
            1 -> "hearts"
            2 -> "clubs"
            3 -> "diamonds"
            else -> "error"
        }
        return "${suit}_$cardnumS"
    }
    fun checkEnd(): Boolean{
        for (i in 0 until AIhands.size){
            if (activeAIs[i]){
                if (playerFolded){
                    Toast.makeText(context, "Player $i, Wins $$totalBet",Toast.LENGTH_SHORT).show()
                    return false
                }
                else return false
            }
        }

        if (!playerFolded){
            Toast.makeText(context, "You Win $$totalBet",Toast.LENGTH_SHORT).show()
            return true
        }
        return false
    }
    fun determineHand(cards: MutableList<String>): Int{


        var legibleCards: MutableList<List<String>> = mutableListOf()
        for (card in cards){
            legibleCards.add( card.split("_"))
        }
        var suits: MutableMap<String, Int> = mutableMapOf(
            "hearts" to 0,
            "spades" to 0,
            "clubs" to 0,
            "diamonds" to 0
        )
        val values: MutableMap<Int, Int> = mutableMapOf()

        for (card in legibleCards){
            suits[card[0]] = suits[card[0]]!! + 1
            val cardVal = when(card[1]){
                "j" -> 11
                "q" -> 12
                "k" -> 13
                "a" -> 14
                else -> card[1].toInt()
            }
            values.set(cardVal, values.getOrDefault(cardVal, 0)+1)
        }

        // has flush
        val hasFlush: Boolean =  (suits.values.any{it >= 5})

        // has straight
        val keys = values.keys.sorted().reversed()
        var straightCount = 1
        var hasStraight = false
        var straightEnd = -1

        for (i in 0 until keys.size - 1) {
            if (keys[i] - 1 == keys[i + 1]) {
                straightCount++
                if (straightCount >= 5) {
                    straightEnd = i
                    hasStraight = true
                    break
                }
            } else {
                straightCount = 1
            }
        }
        val bicycleStraight = straightCount == 4 && keys[0] == 14
        if (hasFlush && hasStraight){
            //check royal flush and straight flush
            var straightFlushCounter = 0
            Log.d("end", straightEnd.toString())
            for (i in (straightEnd-3) until keys.size ){
                val flushSuit = suits.maxBy { it.value }.key
                val exists = cards.contains("${flushSuit}_${keys[i]}")
                if (exists){straightFlushCounter++}else{straightFlushCounter = 0;straightEnd = i+5}
                if (straightFlushCounter == 5){
                    return 800 + keys[straightEnd-3]
                }
            }

        }
        if (values.values.contains(4)){
            //4 of a kind 400+
            return 700 + values.filterValues{it == 4}.keys.max()
        }
        if (values.values.contains(3)){
            if (values.values.contains(2)){
                // full house 300+ depending on strength of full house
                return 600+values.filterValues{it == 3}.keys.max()+values.filterValues{it == 2}.keys.max()
            }
            if (hasFlush){return 500+values.keys.sum()}
            if (bicycleStraight){return 400}
            if (hasStraight){return 401+keys[straightEnd-4]}
                // three of a kind with values 200+ depending on strength of three of a kind
            return 300 + values.filterValues{it == 3}.keys.sorted().reversed().max()

        }
        var numberOfPairs = when(values.values.groupingBy{it}.eachCount()[2]){
            null -> 0
            else -> values.values.groupingBy{it}.eachCount()[2]!!
        }
        if (numberOfPairs >= 2){
            // twopair 100+ depending on strength of twopair
            val valuesWithPair = values.filterValues{it == 2}.keys.sorted().reversed()
            return 200 + valuesWithPair[0] + valuesWithPair[1]
        }

        if (numberOfPairs == 1){
            // pair 15-27 depending on strength of pair
            return 100 + values.filterValues{it == 2}.keys.max()

            // pair
        }
        // highcard num depending on strength of highcard
        return values.keys.max()


    }
    fun reset(){
        totalBet = 0
        centerCards = mutableListOf()
        handCards = mutableListOf()
        turn = 0
        deck = (0..51).toMutableList()
        AIhands = mutableListOf()
        activeAIs = mutableListOf(true,true,true,true,true,true)
        if (arguments?.getInt("numOpponents") != null) {
            for (i in 0 until requireArguments().getInt("numOpponents") + 1){
                val hand: MutableList<String> = mutableListOf()
                hand.add(pickCard())
                hand.add(pickCard())
                AIhands.add(hand)
            }
        }else{
            for (i in 0 until 4){
                val hand: MutableList<String> = mutableListOf()
                hand.add(pickCard())
                hand.add(pickCard())
                AIhands.add(hand)
            }
        }
        var card: String = pickCard()
        handCards.add(card)
        val context = requireContext()
        var imgID = resources.getIdentifier(card, "drawable", context.packageName)
        binding.HandCard1.setImageResource(imgID)
        card = pickCard()
        handCards.add(card)
        imgID = resources.getIdentifier(card, "drawable", context.packageName)
        binding.HandCard2.setImageResource(imgID)

        imgID = resources.getIdentifier("back_dark", "drawable", context.packageName)
        binding.CenterCard1.setImageResource(imgID)
        binding.CenterCard2.setImageResource(imgID)
        binding.CenterCard3.setImageResource(imgID)
        binding.CenterCard4.setImageResource(imgID)
        binding.CenterCard5.setImageResource(imgID)

    }
}