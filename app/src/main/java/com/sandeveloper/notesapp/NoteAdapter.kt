package com.sandeveloper.notesapp

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sandeveloper.notesapp.databinding.NoteItemBinding
import com.sandeveloper.notesapp.models.NoteResponse
import kotlin.random.Random

class NoteAdapter(private val onNoteClicked: (NoteResponse) -> Unit) :ListAdapter<NoteResponse,NoteAdapter.NoteViewHolder>(ComparatorDiffUtil()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = NoteItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = getItem(position)
        note?.let {
            holder.bind(it)
        }
    }

    inner class NoteViewHolder(private val binding: NoteItemBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(note:NoteResponse){
            binding.title.text= note.title
            binding.desc.text = note.description
           binding.layout.setBackgroundColor(getRandomColor())
            //click handler on each note to open note_item
            binding.root.setOnClickListener{
                onNoteClicked(note)
            }
        }
    }
    fun getRandomColor(): Int {
        val random = Random.Default
        return Color.rgb(random.nextInt(128,256), random.nextInt(128,256), random.nextInt(128,256))
    }

    class ComparatorDiffUtil:DiffUtil.ItemCallback<NoteResponse>() {
        override fun areItemsTheSame(oldItem: NoteResponse, newItem: NoteResponse): Boolean {
            return oldItem._id==newItem._id
        }

        override fun areContentsTheSame(oldItem: NoteResponse, newItem: NoteResponse): Boolean {
            return oldItem==newItem
        }

    }
}