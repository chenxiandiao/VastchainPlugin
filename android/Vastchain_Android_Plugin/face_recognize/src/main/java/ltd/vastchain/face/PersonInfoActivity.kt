package ltd.vastchain.face

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ltd.vastchain.face.databinding.ActivityPersonInfoBinding

/**
 * Created by admin on 2021/10/8.
 */
class PersonInfoActivity : AppCompatActivity() {

	companion object {
		fun start(context: Context) {
			val intent = Intent(context, PersonInfoActivity::class.java)
			context.startActivity(intent)
		}
	}

	private lateinit var activityPersonInfoBinding: ActivityPersonInfoBinding

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		activityPersonInfoBinding = ActivityPersonInfoBinding.inflate(layoutInflater)
		setContentView(activityPersonInfoBinding.root)
		initListener()
	}

	private fun initListener() {

		activityPersonInfoBinding.tvSubmit.setOnClickListener {
			val idCard = activityPersonInfoBinding.etIdCard.text.toString()
			val name = activityPersonInfoBinding.etName.text.toString()

			if (idCard.isNullOrEmpty() || name.isNullOrEmpty()) {
				return@setOnClickListener
			}

			FaceIndexActivity.start(this, idCard, name)
		}
	}


}