package com.pos_terminal.tamaktime_temirnal.di

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.pos_terminal.tamaktime_temirnal.common.CardUUIDInteractor
import com.pos_terminal.tamaktime_temirnal.common.Constants.BASE_URL
import com.pos_terminal.tamaktime_temirnal.common.Constants.NETWORK_TIMEOUT
import com.pos_terminal.tamaktime_temirnal.common.LanguagePreference
import com.pos_terminal.tamaktime_temirnal.data.remote.apiservice.CategoryService
import com.pos_terminal.tamaktime_temirnal.data.remote.apiservice.DocsService
import com.pos_terminal.tamaktime_temirnal.data.remote.apiservice.OrderService
import com.pos_terminal.tamaktime_temirnal.data.remote.apiservice.ProductService
import com.pos_terminal.tamaktime_temirnal.data.remote.apiservice.QRService
import com.pos_terminal.tamaktime_temirnal.data.remote.apiservice.StudentLimitService
import com.pos_terminal.tamaktime_temirnal.data.remote.apiservice.StudentService
import com.pos_terminal.tamaktime_temirnal.data.remote.apiservice.UserService
import com.pos_terminal.tamaktime_temirnal.data.remote.local.UserStoreImpl
import com.pos_terminal.tamaktime_temirnal.data.repositories.category.CategoryRemoteDataSource
import com.pos_terminal.tamaktime_temirnal.data.repositories.category.CategoryRepository
import com.pos_terminal.tamaktime_temirnal.data.repositories.documents.DocumentRemoteDataSourse
import com.pos_terminal.tamaktime_temirnal.data.repositories.documents.DocumentRepository
import com.pos_terminal.tamaktime_temirnal.data.repositories.order.OrderRemoteDataSource
import com.pos_terminal.tamaktime_temirnal.data.repositories.order.OrderRepository
import com.pos_terminal.tamaktime_temirnal.data.repositories.product.ProductRemoteDataSource
import com.pos_terminal.tamaktime_temirnal.data.repositories.product.ProductRepository
import com.pos_terminal.tamaktime_temirnal.data.repositories.student.StudentRemoteDataSource
import com.pos_terminal.tamaktime_temirnal.data.repositories.student.StudentRepository
import com.pos_terminal.tamaktime_temirnal.data.repositories.student.limit.StudentLimitRemoteDataSource
import com.pos_terminal.tamaktime_temirnal.data.repositories.student.limit.StudentLimitRepository
import com.pos_terminal.tamaktime_temirnal.data.repositories.student.qr.QrOrderRemoteDataSource
import com.pos_terminal.tamaktime_temirnal.data.repositories.student.qr.QrOrderRepository
import com.pos_terminal.tamaktime_temirnal.data.repositories.user.UserRemoteDataSource
import com.pos_terminal.tamaktime_temirnal.data.repositories.user.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return interceptor
    }

    @Provides
    @Singleton
    fun provideCardUUIDInteractor(): CardUUIDInteractor {
        return CardUUIDInteractor()
    }

    @Provides
    @Singleton
    fun provideLanguageInterceptor(
        languagePreference: LanguagePreference,
    ): LanguageInterceptor = LanguageInterceptor(languagePreference)

    @Provides
    @Singleton
    fun provideLanguagePreference(@ApplicationContext context: Context): LanguagePreference {
        return LanguagePreference(context)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(NETWORK_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(NETWORK_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(NETWORK_TIMEOUT, TimeUnit.SECONDS)
            .callTimeout(NETWORK_TIMEOUT, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder().create()

    @Provides
    @Singleton
    fun provideConverterFactory(): GsonConverterFactory =
        GsonConverterFactory.create()

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory,
    ): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .addConverterFactory(gsonConverterFactory)
            .build()
    }

    @Provides
    @Singleton
    fun provideUserService(retrofit: Retrofit): UserService =
        retrofit.create(UserService::class.java)

    @Provides
    @Singleton
    fun provideUserRemoteDataSource(userService: UserService): UserRemoteDataSource =
        UserRemoteDataSource(userService)

    @Provides
    @Singleton
    fun provideUserStoreImpl(@ApplicationContext appContext: Context): UserStoreImpl =
        UserStoreImpl(appContext)

    @Provides
    @Singleton
    fun provideUserRepository(
        local: UserStoreImpl,
        remote: UserRemoteDataSource,
        languagePreference: LanguagePreference,
    ): UserRepository =
        UserRepository(local, remote, languagePreference)

    @Provides
    @Singleton
    fun provideCategoryService(retrofit: Retrofit): CategoryService =
        retrofit.create(CategoryService::class.java)

    @Provides
    @Singleton
    fun provideCategoryRemoteDataSource(categoryService: CategoryService): CategoryRemoteDataSource =
        CategoryRemoteDataSource(categoryService)

    @Provides
    @Singleton
    fun provideCategoryRepository(remote: CategoryRemoteDataSource): CategoryRepository =
        CategoryRepository(remote)

    @Provides
    @Singleton
    fun provideProductService(retrofit: Retrofit): ProductService =
        retrofit.create(ProductService::class.java)

    @Provides
    @Singleton
    fun provideProductRemoteDataSource(productService: ProductService): ProductRemoteDataSource =
        ProductRemoteDataSource(productService)

    @Provides
    @Singleton
    fun provideProductRepository(remote: ProductRemoteDataSource): ProductRepository =
        ProductRepository(remote)

    @Provides
    @Singleton
    fun provideOrderService(retrofit: Retrofit): OrderService =
        retrofit.create(OrderService::class.java)

    @Provides
    @Singleton
    fun provideOrderRemoteDataSource(orderService: OrderService): OrderRemoteDataSource =
        OrderRemoteDataSource(orderService)

    @Provides
    @Singleton
    fun provideOrderRepository(remote: OrderRemoteDataSource): OrderRepository =
        OrderRepository(remote)

    @Provides
    @Singleton
    fun provideStudentService(retrofit: Retrofit): StudentService =
        retrofit.create(StudentService::class.java)

    @Provides
    @Singleton
    fun provideStudentRemoteDataSource(studentService: StudentService): StudentRemoteDataSource =
        StudentRemoteDataSource(studentService)

    @Provides
    @Singleton
    fun provideStudentRepository(remote: StudentRemoteDataSource): StudentRepository =
        StudentRepository(remote)

    @Provides
    @Singleton
    fun provideStudentLimitService(retrofit: Retrofit): StudentLimitService =
        retrofit.create(StudentLimitService::class.java)

    @Provides
    @Singleton
    fun provideStudentLimitRemoteDataSource(studentLimitService: StudentLimitService): StudentLimitRemoteDataSource =
        StudentLimitRemoteDataSource(studentLimitService)

    @Provides
    @Singleton
    fun provideStudentLimitRepository(remote: StudentLimitRemoteDataSource): StudentLimitRepository =
        StudentLimitRepository(remote)

    @Provides
    @Singleton
    fun provideStudentQR(retrofit: Retrofit): QRService =
        retrofit.create(QRService::class.java)

    @Provides
    @Singleton
    fun provideStudentQrRemoteDataSource(qrService: QRService): QrOrderRemoteDataSource =
        QrOrderRemoteDataSource(qrService)

    @Provides
    @Singleton
    fun provideStudentQrRepository(remote: QrOrderRemoteDataSource): QrOrderRepository =
        QrOrderRepository(remote)

    @Provides
    @Singleton
    fun provideDocs(retrofit: Retrofit): DocsService =
        retrofit.create(DocsService::class.java)

    @Provides
    @Singleton
    fun provideDocumentRepository(remote: DocumentRemoteDataSourse): DocumentRepository =
        DocumentRepository(remote)

}