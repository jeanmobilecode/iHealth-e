package com.logicalayer.ihealthe.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.logicalayer.ihealthe.R

class AdapterInstructions(
    private var instructionsList: List<Instructions>
) : RecyclerView.Adapter<AdapterInstructions.InstructionViewHolder>() {

    inner class InstructionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val instructionText: TextView = itemView.findViewById(R.id.instructionIndexText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InstructionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_instruction, parent, false)
        return InstructionViewHolder(view)
    }

    override fun onBindViewHolder(holder: InstructionViewHolder, position: Int) {
        val instruction = instructionsList[position]
        holder.instructionText.text = "${instruction.step}. ${instruction.text}"
    }

    override fun getItemCount(): Int {
        return instructionsList.size
    }

    fun updateList(newList: List<Instructions>) {
        instructionsList = newList
        notifyDataSetChanged()
    }
}
