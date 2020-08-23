package com.jay.presentation;

import com.jay.dao.BankAccountDao;
import com.jay.dao.FixedDepositDao;
import com.jay.domain.BankAccountDetails;
import com.jay.domain.FixedDepositDetails;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/jdbc")
public class FixedDepositController {

  @Autowired
  private TransactionTemplate transactionTemplate;

  @Autowired
  @Qualifier(value = "fixedDepositDao")
  private FixedDepositDao myFixedDepositDao;

  @Autowired
  private BankAccountDao bankAccountDao;

  @RequestMapping(value = "/txTest", method = RequestMethod.GET)
  String txTest(){

    BankAccountDetails bankAccountDetails = new BankAccountDetails();
    bankAccountDetails.setBalanceAmount(100000);
    bankAccountDetails.setLastTransactionTimestamp(new Date());

    int bankAccountId = bankAccountDao.createBankAccount(bankAccountDetails);

    System.out.println("bankAccountId ==>" + bankAccountId);

    FixedDepositDetails fdd = new FixedDepositDetails();

    fdd.setActive("Y");
    fdd.setFdAmount(1500);
    fdd.setBankAccountId(bankAccountId);
    fdd.setFdCreationDate(new Date());
    fdd.setTenure(12);



    transactionTemplate
        .execute(new TransactionCallback<FixedDepositDetails>() {

          @Override
          public FixedDepositDetails doInTransaction(
              TransactionStatus status) {
            try {
              int t1 = myFixedDepositDao.createFixedDeposit(fdd);

              System.out.print("t1 ==>" + t1);

              bankAccountDao.subtractFromAccount(
                  fdd.getBankAccountId(), fdd.getFdAmount());
            } catch (Exception e) {
              e.printStackTrace();
              status.setRollbackOnly();
            }
            return fdd;
          }
        });


    System.out.println("is null ==>" + transactionTemplate.getTransactionManager().toString());
    return "Success";
  }
}
