package com.example.macroup.recyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.macroup.R
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class AdapterInstructions(options: FirestoreRecyclerOptions<Instructions>) :
    FirestoreRecyclerAdapter<Instructions, AdapterInstructions.InstructionViewHolder>(options) {

    inner class InstructionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val instructionText: TextView = itemView.findViewById(R.id.instructionIndexText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InstructionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_instruction, parent, false)
        return InstructionViewHolder(view)
    }

    override fun onBindViewHolder(holder: InstructionViewHolder, position: Int, model: Instructions) {
        holder.instructionText.text = "${model.step}. ${model.text}"
    }
}
