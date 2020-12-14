package com.meritamerica.assignment6.repository;

import java.util.*;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.jpa.repository.support.*;
import com.meritamerica.assignment6.*;
import com.meritamerica.assignment6.model.*;

public interface CheckingAccountRepository extends JpaRepository<CheckingAccount, Integer>{
	List<CheckingAccount> findAccountHolderIDs(long id);
}
