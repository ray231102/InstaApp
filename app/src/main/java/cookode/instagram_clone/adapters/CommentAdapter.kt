package cookode.instagram_clone.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import cookode.instagram_clone.Adapter.User
import cookode.instagram_clone._clone.R
import de.hdodenhof.circleimageview.CircleImageView

class CommentAdapter(private val mContext: Context, private val mComments: MutableList<cookode.instagram_clone.model.Comment>):RecyclerView.Adapter<CommentAdapter.ClassViewHolder>() {


    private var firebaseUser: FirebaseUser? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CommentAdapter.ClassViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.comment_item_layout, parent, false)
        return ClassViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mComments.size
    }

    override fun onBindViewHolder(holder: CommentAdapter.ClassViewHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser
        val comment = mComments[position]
        holder.commentTV.text =comment.comment
        getUserInfo(holder.imageProfileComment, holder.userNameCommentTV, comment.publisher)
    }

    class ClassViewHolder(@NonNull itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        var imageProfileComment: CircleImageView = itemView.findViewById(R.id.user_profile_image_comment)
        var userNameCommentTV: TextView = itemView.findViewById(R.id.user_name_comment)
        var commentTV: TextView = itemView.findViewById(R.id.comment_comment)

    }

    private fun getUserInfo(imageProfileComment: CircleImageView, userNameCommentTV: TextView, publisher: String) {

        val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(publisher)

        userRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(User::class.java)
                    Picasso.get().load(user?.image).placeholder(R.drawable.profile).into(imageProfileComment)
                    userNameCommentTV.text = user?.username
                }
            }
        })

    }
}