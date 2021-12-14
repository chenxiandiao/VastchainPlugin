package ltd.vastchain.face

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import ltd.vastchain.face.databinding.ActivityPersonInfoBinding
import ltd.vastchain.face.utils.SPConstants
import ltd.vastchain.face.utils.SPUtils

/**
 * Created by admin on 2021/10/8.
 */
class PersonInfoActivity : AppCompatActivity() {

	companion object {
		fun start(context: Context) {
			SPUtils.initContext(context)
			val name = SPUtils.getString(SPConstants.NAME)
			val idCard = SPUtils.getString(SPConstants.IDCARD)
			Log.e("cxd", name)
			Log.e("cxd", idCard)
			if(name.isNotEmpty() && idCard.isNotEmpty()) {
				FaceIndexActivity.start(context, idCard, name)
			} else {
				val intent = Intent(context, PersonInfoActivity::class.java)
				context.startActivity(intent)
			}

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

		activityPersonInfoBinding.toolbar.tvTitle.text = "个人信息"
		activityPersonInfoBinding.toolbar.ivBack.setOnClickListener{
			finish()
		}

		activityPersonInfoBinding.tvSubmit.setOnClickListener {
			val idCard = activityPersonInfoBinding.etIdCard.text.toString()
			val name = activityPersonInfoBinding.etName.text.toString()

			if (idCard.isNullOrEmpty() || name.isNullOrEmpty()) {
				return@setOnClickListener
			}

			SPUtils.put(SPConstants.IDCARD, idCard)
			SPUtils.put(SPConstants.NAME, name)

			FaceIndexActivity.start(this, idCard, name)
			finish()
		}
	}


}