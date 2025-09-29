package com.hospital.management.dao.interfaces;


import com.hospital.management.models.Bill;
import java.util.List;

public interface BillDAO {
    Bill getBillById(int id);
    List<Bill> getAllBills();
    boolean createBill(Bill bill);
    boolean updateBill(Bill bill);
    boolean deleteBill(int id);
}
