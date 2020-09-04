package cookode.instagram_clone.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import cookode.instagram_clone.R
import cookode.instagram_clone.model.Post

class MyImageAdapter(private val mContext: Context, mPost: List<Post>)
    : RecyclerView.Adapter<MyImageAdapter.ViewHolder?>(){

    private var mPost: List<Post>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        TODO("Not yet implemented")
    }

    override fun getItemCount() = mPost!!.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post: Post = mPost!![position]

        Picasso.get().load(post.postImage).into(holder.postImage)
    }

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        var postImage: ImageView = itemView.findViewById(R.id.post_image_grid)
    }
}