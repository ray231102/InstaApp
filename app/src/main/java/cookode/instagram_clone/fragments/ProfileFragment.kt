package cookode.instagram_clone.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

import cookode.instagram_clone.R
import cookode.instagram_clone.AccountSettingActivity
import cookode.instagram_clone.adapter.MyImageAdapter
import cookode.instagram_clone.model.Post
import cookode.instagram_clone.model.User
import kotlinx.android.synthetic.main.fragment_profile.view.*

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment() {

    private lateinit var profileId : String
    private lateinit var firebaseUser : FirebaseUser

    var postListGrid: MutableList<Post>? = null
    var myImagesAdapter: MyImageAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewProfile = inflater.inflate(R.layout.fragment_profile, container, false)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        val pref = context?.getSharedPreferences("Prefs", Context.MODE_PRIVATE)
        if (pref != null) {
            this.profileId = pref.getString("profileId", "none")!!
        }

        if (profileId == firebaseUser.uid) {
            view?.btn_edit_account?.text = "Edit Profile"
        } else if (profileId != firebaseUser.uid) {
            cekFollowAndFollowingButtonStatus()
        }
        viewProfile.btn_edit_account.setOnClickListener {
            startActivity(Intent(context, AccountSettingActivity::class.java))
        }

        var recyclerViewUploadImages: RecyclerView? = null
        recyclerViewUploadImages = viewProfile.findViewById(R.id.recyclerview_upload_picimage)
        recyclerViewUploadImages?.setHasFixedSize(true)
        val linearLayoutManager = GridLayoutManager(context,3)
        recyclerViewUploadImages?.layoutManager = linearLayoutManager

        postListGrid = ArrayList()
        myImagesAdapter = context?.let {
            MyImageAdapter(it, postListGrid as ArrayList<Post>) }
        recyclerViewUploadImages?.adapter = myImagesAdapter

        getFollowers()
        getFollowings()
        userInfo()

        return viewProfile
    }

    private fun cekFollowAndFollowingButtonStatus() {

        firebaseUser?.uid.let { it1 ->
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(it1.toString())
                .child("Following")
        }.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {

                if (p0.child(profileId).exists()) {
                    view?.btn_edit_account?.text = "Following"
                } else {
                    view?.btn_edit_account?.text = "Follow"
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun getFollowers()
    {
        val followersRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(profileId)
            .child("Followers")

        followersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.exists()){
                    view?.txt_totalFollowers?.text = p0.childrenCount.toString()
                }
            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun getFollowings()
    {
        val followersRef = FirebaseDatabase.getInstance().reference
            //sesuai yang berada di firebase
            .child("Follow").child(profileId)
            .child("Following")

        followersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                if (p0.exists()){
                    view?.txt_totalFollowing?.text = p0.childrenCount.toString()
                }
            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun userInfo() {
        val usersRef = FirebaseDatabase.getInstance().getReference()
            .child("Users").child(profileId)

        usersRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {

                if (p0.exists()){
                    val user = p0.getValue<User>(User::class.java)

                    Picasso.get().load(user?.image).placeholder(R.drawable.profile)
                        .into(view?.profile_image_gbr_frag)
                    view?.profile_fragment_username?.text = user?.username
                    view?.txt_full_namaProfile?.text = user?.fullname
                    view?.txt_bio_profile?.text = user?.bio
                }
            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}