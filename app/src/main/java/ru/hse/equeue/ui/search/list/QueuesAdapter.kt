package ru.hse.equeue.ui.search.list

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import ru.hse.equeue.R
import ru.hse.equeue.databinding.ListItemQueueBinding
import ru.hse.equeue.model.Queue
import ru.hse.equeue.ui.queues.ActiveQueueViewModel
import ru.hse.equeue.ui.search.SearchViewModel

class QueuesAdapter(
    private val navController: NavController,
    private val queueViewModel: ActiveQueueViewModel,
    private val searchViewModel: SearchViewModel
) : RecyclerView.Adapter<QueuesAdapter.QueueViewHolder>(), View.OnClickListener {

    var queus: List<Queue> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var prevUpdateSize = -1

    class QueueViewHolder(
        val binding: ListItemQueueBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QueueViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemQueueBinding.inflate(inflater, parent, false)
        binding.root.setOnClickListener(this)
        return QueueViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QueueViewHolder, position: Int) {
        if (position != prevUpdateSize && position == queus.size - 1) {
            prevUpdateSize = position
            searchViewModel.getPageQueues()
        }
        val queue = queus[position]
        with(holder.binding) {
            holder.itemView.tag = queue
            queueNameTextView.text = queue.name
            addressQueueTextView.text = queue.address
            averageTimeQueueTextView.text =
                String.format("%.1f", queue.status.currentUsersCount * queue.status.serviceTime)
            if (queue.photoUrl.isNotBlank()) {
                Picasso.get().load(queue.photoUrl).into(photoQueueImageView);
            } else {
                photoQueueImageView.setImageResource(R.drawable.queue_default_image_24)
            }
        }
    }

    override fun getItemCount(): Int = queus.size


    override fun onClick(v: View?) {
        val queue = v?.tag as Queue
        queueViewModel.selectedQueue.value = queue
        navController.navigate(R.id.action_listSearchFragment_to_queueFragment)
    }


}