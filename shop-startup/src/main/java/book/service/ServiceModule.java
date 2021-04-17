package book.service;

import com.google.inject.AbstractModule;

import book.service.impl.AdminServiceImpl;
import book.service.impl.AnalysisServiceImpl;
import book.service.impl.BookServiceImpl;
import book.service.impl.ReaderServiceImpl;
import book.service.impl.TypeServiceImpl;

public class ServiceModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(AdminService.class).to(AdminServiceImpl.class);
        bind(AnalysisService.class).to(AnalysisServiceImpl.class);
        bind(BookService.class).to(BookServiceImpl.class);
        bind(ReaderService.class).to(ReaderServiceImpl.class);
        bind(TypeService.class).to(TypeServiceImpl.class);
    }
}
