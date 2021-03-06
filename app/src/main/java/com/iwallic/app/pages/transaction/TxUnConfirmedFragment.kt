package com.iwallic.app.pages.transaction

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.iwallic.app.R
import com.iwallic.app.adapters.TransactionAdapter
import com.iwallic.app.base.BaseLazyFragment
import com.iwallic.app.pages.common.BrowserActivity
import com.iwallic.app.states.UnconfirmedState
import com.iwallic.app.utils.DialogUtils
import com.scwang.smartrefresh.layout.SmartRefreshLayout

class TxUnConfirmedFragment : BaseLazyFragment() {
    private lateinit var txRV: RecyclerView
    private lateinit var txSRL: SmartRefreshLayout
    private lateinit var txAdapter: TransactionAdapter
    private lateinit var txManager: LinearLayoutManager
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_tx_un_confirmed, container, false)
        initDOM(view)
        initListener()
        notifyCreatedView()
        return view
    }

    override fun onResolve() {
        val loader = DialogUtils.loader(context!!)
        UnconfirmedState.init(context, {
            txAdapter.set(it)
            loader.dismiss()
        }, {
            loader.dismiss()
            if (!DialogUtils.error(context, it)) {
                Toast.makeText(context, "$it", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun initDOM(view: View) {
        txRV = view.findViewById(R.id.transaction_list)
        txSRL = view.findViewById(R.id.transaction_pager)

        txAdapter = TransactionAdapter(arrayListOf())
        txManager = LinearLayoutManager(context!!)
        txRV.layoutManager = txManager
        txRV.adapter = txAdapter
    }

    private fun initListener() {
        txSRL.setOnRefreshListener { _ ->
            UnconfirmedState.refresh(context, {
                txSRL.finishRefresh(true)
                txAdapter.set(it)
            }, {
                txSRL.finishRefresh()
                if (!DialogUtils.error(context, it)) {
                    Toast.makeText(context, "$it", Toast.LENGTH_SHORT).show()
                }
            })
        }
        txSRL.setOnLoadMoreListener { _ ->
            UnconfirmedState.older(context, {
                txAdapter.push(it)
                if (it.size > 0) {
                    txSRL.finishLoadMore(true)
                } else {
                    txSRL.finishLoadMoreWithNoMoreData()
                }
            }, {
                txSRL.finishLoadMore()
                if (!DialogUtils.error(context, it)) {
                    Toast.makeText(context, "$it", Toast.LENGTH_SHORT).show()
                }
            })
        }
        txAdapter.setOnTxEnterListener {
            val intent = Intent(context, BrowserActivity::class.java)
            intent.putExtra("url", "https://blolys.com/#/transaction/${txAdapter.getItem(it).txid}")
            context?.startActivity(intent)
        }
        txAdapter.setOnTxCopyListener {
            copy(txAdapter.getItem(it).txid, "txid")
            vibrate()
        }
    }
}
