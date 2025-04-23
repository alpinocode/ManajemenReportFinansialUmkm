package com.example.manajemenreportfinansialumkm.ui.viewModelFactory

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.manajemenreportfinansialumkm.SignUpAndSignOutViewModel
import com.example.manajemenreportfinansialumkm.data.Repository
import com.example.manajemenreportfinansialumkm.di.Injection
import com.example.manajemenreportfinansialumkm.ui.home.HomeViewModel
import com.example.manajemenreportfinansialumkm.ui.laporanKeuangan.LaporanKeuanganViewModel
import com.example.manajemenreportfinansialumkm.ui.notification.NotificationViewModel
import com.example.manajemenreportfinansialumkm.ui.product.ProductViewModel
import com.example.manajemenreportfinansialumkm.ui.register.RegisterViewModel
import com.example.manajemenreportfinansialumkm.ui.stock.StockViewModel
import com.example.manajemenreportfinansialumkm.ui.transaction.TrasanctionViewModel

class ViewModelFactory(private val repository: Repository) : ViewModelProvider.NewInstanceFactory() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignUpAndSignOutViewModel::class.java)){
            return SignUpAndSignOutViewModel(repository) as T
        } else if(modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(repository) as T
        } else if(modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(repository) as T
        } else if(modelClass.isAssignableFrom(StockViewModel::class.java)) {
            return StockViewModel(repository) as T
        } else if (modelClass.isAssignableFrom(ProductViewModel::class.java)) {
            return ProductViewModel(repository) as T
        } else if(modelClass.isAssignableFrom(NotificationViewModel::class.java)) {
            return NotificationViewModel(repository) as T
        } else if(modelClass.isAssignableFrom(TrasanctionViewModel::class.java)) {
            return TrasanctionViewModel(repository) as T
        } else if(modelClass.isAssignableFrom(LaporanKeuanganViewModel::class.java)) {
            return LaporanKeuanganViewModel(repository) as T
        }
        throw Exception("Unknown Class ${modelClass.name}")
    }

    companion object {
        @Volatile
        var INSTANCE:ViewModelFactory? = null

        fun getInstance(context:Context): ViewModelFactory =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: ViewModelFactory(Injection.provideRepository(context))
            }.also { INSTANCE = it }
    }
}