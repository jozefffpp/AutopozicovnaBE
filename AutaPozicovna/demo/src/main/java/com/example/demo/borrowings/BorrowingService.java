package com.example.demo.borrowings;

import com.example.demo.cars.CarEntity;
import com.example.demo.cars.CarRepository;
import com.example.demo.cars.CarService;
import com.example.demo.customers.CustomerEntity;
import com.example.demo.customers.CustomerRepository;
import com.example.demo.customers.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
public class BorrowingService {
    private final BorrowingRepository borrowingRepository;
    private final CustomerRepository customerRepository;
    private final CarRepository carRepository;

    @Autowired
    private final CustomerService customerService;
    @Autowired
    private final CarService carService;


    public BorrowingService(BorrowingRepository borrowingRepository, CustomerService customerService, CarService carService, CarRepository carRepository, CustomerRepository customerRepository) {
        this.borrowingRepository = borrowingRepository;
        this.customerRepository = customerRepository;
        this.carRepository = carRepository;
        this.customerService = customerService;
        this.carService = carService;
    }

    private static BorrowingDto mapToBorrowingDto(BorrowingEntity borrowingEntity) {
        BorrowingDto borrowingDto = new BorrowingDto();

        borrowingDto.setBorrowingId(borrowingEntity.getBorrowingId());
        borrowingDto.setCustomerId((long) borrowingEntity.getBorrower().getCustomer_id());
        borrowingDto.setCarId((long) borrowingEntity.getBorrowedCar().getId());
        borrowingDto.setBorrowingStartDate(borrowingEntity.getBorrowingStartDate());
        borrowingDto.setBorrowingEndDate(borrowingEntity.getBorrowingEndDate());

        return borrowingDto;
    }

    @Transactional
    public BorrowingDto getBorrowing(int borrowingId) {
        Optional<BorrowingEntity> byId = borrowingRepository.findById(borrowingId);
        if (byId.isPresent()) {
            return mapToBorrowingDto(byId.get());
        }
        return null;
    }

    @Transactional
    public List<BorrowingDto> getBorrowings(Long borrowingId) {
        List<BorrowingDto> ls = new LinkedList<>();
        for (BorrowingEntity be1 : borrowingRepository.findAll()) {
            BorrowingDto be2 = mapToBorrowingDto(be1);
            ls.add(be2);
        }
        return ls;
    }

    @Transactional
    public int createBorrowing(BorrowingDto borrowingDto) {
        BorrowingEntity borrowingEntity = new BorrowingEntity();

        Optional <CustomerEntity> c = customerRepository.findById(Math.toIntExact(borrowingDto.getCustomerId()));
        Optional <CarEntity> b = carRepository.findById(Math.toIntExact(borrowingDto.getCarId()));

        if(c.isPresent()) {
            borrowingEntity.setBorrower(c.get());
        }

        if(b.isPresent()) {
            borrowingEntity.setBorrowedCar(b.get());
        }
        borrowingEntity.setBorrowingStartDate(borrowingDto.getBorrowingStartDate());
        borrowingEntity.setBorrowingEndDate(borrowingDto.getBorrowingEndDate());

        this.borrowingRepository.save(borrowingEntity);

        return borrowingEntity.getBorrowingId();
    }

    @Transactional
    public void updateBorrowing(int borrowingId, BorrowingDto borrowingDto) {
        Optional<BorrowingEntity> borrowingEntity = borrowingRepository.findById(borrowingId);

        if (borrowingEntity.isPresent()) {

            Optional <CustomerEntity> c = customerRepository.findById(Math.toIntExact(borrowingDto.getCustomerId()));
            Optional <CarEntity> b = carRepository.findById(Math.toIntExact(borrowingDto.getCarId()));

            if(c.isPresent()) {
                borrowingEntity.get().setBorrower(c.get());
            }

            if(b.isPresent()) {
                borrowingEntity.get().setBorrowedCar(b.get());
            }

            borrowingEntity.get().setBorrowingStartDate(borrowingDto.getBorrowingStartDate());
            borrowingEntity.get().setBorrowingEndDate(borrowingDto.getBorrowingEndDate());

//            borrowingDto.setBorrowingStartDate(borrowingEntity.get().getBorrowingStartDate());
//            borrowingDto.setBorrowingEndDate(borrowingEntity.get().getBorrowingEndDate());

        }
    }

    @Transactional
    public void deleteBorrowing(int borrowingId) {
        Optional<BorrowingEntity> byId = borrowingRepository.findById(borrowingId);
        if (byId.isPresent()) {
            borrowingRepository.delete(byId.get());
        }
    }

//    @Transactional
//    public List<BorrowingEntity> getAllBorrowings(){
//        return borrowingRepository.findAll();
//    }
}
