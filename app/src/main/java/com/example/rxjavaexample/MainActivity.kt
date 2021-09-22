package com.example.rxjavaexample

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.rxjavaexample.databinding.ActivityMainBinding
import com.example.rxjavaexample.domain.data.database.AppDataBase
import com.example.rxjavaexample.domain.model.History
import com.example.rxjavaexample.presentation.ui.recycler.Recycler
import com.example.rxjavaexample.presentation.ui.recycler.RecyclerAdapter
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableEmitter
import io.reactivex.rxjava3.core.ObservableOnSubscribe
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity(), Recycler {

    private lateinit var binding: ActivityMainBinding
    private lateinit var recyclerAdapter: RecyclerAdapter
    private lateinit var db : AppDataBase
    val TAG = "MainActivity - 로그"
    val list = ArrayList<History>()

    //===========================================

    // 데이터를 추적할 옵저버블을 생
    val observableEditText = Observable
        .create(ObservableOnSubscribe { emitter: ObservableEmitter<String>? ->
            binding.editQuery.addTextChangedListener(
                object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {

                    }

                    override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        emitter?.onNext(s.toString())
                    }

                    override fun afterTextChanged(p0: Editable?) {
                    }

                }
            )
        }
        ).subscribeOn(Schedulers.io())

    // 데이터가 변경될 때 마다 데이터 흐름을 생성 시켜주는 Subject
    val textSubject = BehaviorSubject.create<String>()

    //===========================================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpInit()
        setUpObserver()
//        setUpSubject()


        setUpButton()
    }

    fun setUpInit(){
        recyclerAdapter = RecyclerAdapter(this)

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = recyclerAdapter
        }

        db = Room.databaseBuilder(
            applicationContext,
            AppDataBase::class.java,
            "database-name"
        ).build()

        db.historyDao()
            .findAll()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                list.addAll(it)
                recyclerModifier(it as ArrayList<History>)
            }
    }

    fun setUpObserver(){
        observableEditText
            .filter { it.isNotEmpty() }
            .debounce(200, TimeUnit.MILLISECONDS)
            .subscribe(object : Observer<String>{
            override fun onSubscribe(d: Disposable?) {
            }

            override fun onNext(t: String?) {
                // 안정적으로 데이터의 흐름을 받고 있는 경우
                findByKeyWordDB(t!!)
            }

            override fun onError(e: Throwable?) {
                // 데이터 흐름 도중 에러가 발생하는 경우
            }

            override fun onComplete() {
                // 데이터의 흐름이 끝나는 경오
            }
        })
    }

    fun setUpSubject(){

        binding.editQuery.addTextChangedListener(
            object : TextWatcher{
                override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    textSubject.onNext(s.toString())
                }

                override fun afterTextChanged(p0: Editable?) {
                }

            }
        )

        // 데이터 흐름이 발생 할 때 마다 작업을 처리
        textSubject
            .filter { it.isNotEmpty() }
            .debounce(200, TimeUnit.MILLISECONDS)
            .subscribe {
            findByKeyWordDB(it)
        }
    }

    fun setUpButton(){
        binding.searchButton.setOnClickListener {
            val newHistory = History(null, binding.editQuery.text.toString())

            if (!list.contains(newHistory) && binding.editQuery.text.toString() != "") {
                db.historyDao().insertItem(
                    newHistory
                )
                    .subscribeOn(Schedulers.io()) //I/O 처리 작업을 할 때 사용하는 스케쥴러
                    .observeOn(AndroidSchedulers.mainThread())  // Ui쓰레드에서 동작
                    .subscribe()
                list.add(newHistory)
                recyclerModifier(list)
            }

            binding.editQuery.setText("")

            Log.d(TAG, "onCreate: ")
        }

        binding.deleteButton.setOnClickListener {
            db.historyDao().deleteAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
            list.clear()
        }
    }

    fun recyclerModifier(historyList: ArrayList<History>) {
        recyclerAdapter.submitList(historyList)
        recyclerAdapter.notifyDataSetChanged()

//        binding.recyclerView.layoutParams = recyclerSize()
    }

    fun findByKeyWordDB(s: String){
        db.historyDao()
            .findByKeyWord(s)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                list.addAll(it)
                recyclerModifier(it as ArrayList<History>)
            }
    }

    fun recyclerSize(): RelativeLayout.LayoutParams {
        return RelativeLayout.LayoutParams(0, if (list.size < 6) list.size * 12 else 6 * 12)
    }


    override fun onClickedItem(position: Int) {

    }
}