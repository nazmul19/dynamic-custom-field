/**
 * 
 */
package com.demo.core.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.demo.core.domain.User;

/**
 * @author Nazmul Hassan
 *
 */
@Repository
public interface UserRepository extends PagingAndSortingRepository<User, Long> {
	List<User> findByUsername(@Param("username") String username);
}
