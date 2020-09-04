package cookode.instagram_clone.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import cookode.instagram_clone.R
import cookode.instagram_clone.fragments.ProfileFragment
import cookode.instagram_clone.model.User
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter (private var mContext : Context, private val mUser : List<User>,
                   private var isFragment : Boolean = false) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    private var firebaseUser : FirebaseUser? = FirebaseAuth.getInstance().currentUser

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAdapter.UserViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.user_item_layout, parent, false)
        return UserAdapter.UserViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mUser.size
    }

    override fun onBindViewHolder(holder: UserAdapter.UserViewHolder, position: Int) {
        val user = mUser[position]
        holder.userName.text = user.username
        holder.fullName.text = user.fullname
        Picasso.get().load(user.image).placeholder(R.drawable.profile).into(holder.image)

        cekFollowingStatus(user.uid, holder.btnFollow)

        holder.itemView.setOnClickListener {
            val pref = mContext.getSharedPreferences("Prefs", Context.MODE_PRIVATE).edit()
            pref.putString("profileId", user.uid)
            pref.apply()

            (mContext as FragmentActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ProfileFragment()).commit()
        }

        holder.btnFollow.setOnClickListener {
            if (holder.btnFollow.text.toString() == "Follow") {
                firebaseUser?.uid.let {
                        it1 ->
                    FirebaseDatabase.getInstance().reference
                        .child("Follow").child(it1.toString())
                        .child("Following").child(user.uid)
                        .setValue(true).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                firebaseUser?.uid.let { it1 ->
                                    FirebaseDatabase.getInstance().reference
                                        .child("Follow").child(user.uid)
                                        .child("Followers").child(it1.toString())
                                        .setValue(true).addOnCompleteListener { task ->
                                            if (task.isSuccessful) {

                                            }
                                        }
                                }
                            }
                        }
                }
            } else {
                firebaseUser?.uid.let { it1 ->
                    FirebaseDatabase.getInstance().reference
                        .child("Follow").child(it1.toString())
                        .child("Following").child(user.uid)
                        .removeValue().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                firebaseUser?.uid.let {it1 ->
                                    FirebaseDatabase.getInstance().reference
                                        .child("Follow").child(user.uid)
                                        .child("Follower").child(it1.toString())
                                        .removeValue().addOnCompleteListener { task ->
                                            if (task.isSuccessful) {

                                            }
                                        }
                                }
                            }
                        }
                }
            }
        }
    }

    class UserViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        var userName : TextView = itemView.findViewById(R.id.user_name_search)
        var fullName : TextView = itemView.findViewById(R.id.user_fullname_search)
        var image : CircleImageView = itemView.findViewById(R.id.user_profile_image_search)
        var btnFollow : Button = itemView.findViewById(R.id.follow_btnsearch)
    }

    private fun cekFollowingStatus(uid: String, btnFollow: Button) {
        val followingRef = firebaseUser?.uid.let { it1 ->
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(it1.toString())
                .child("Following")
        }

        followingRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child(uid).exists()) {
                    btnFollow.text = "Following"
                } else {
                    btnFollow.text = "Follow"
                }
            }
        })
    }
}