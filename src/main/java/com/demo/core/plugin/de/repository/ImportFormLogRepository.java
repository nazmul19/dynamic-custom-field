package com.demo.core.plugin.de.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.demo.core.plugin.de.domain.ImportFormLog;

@Repository
public interface ImportFormLogRepository extends JpaRepository<ImportFormLog, Long>{
	
	ImportFormLog findFirstByFilenameOrderByExecutedOnDesc(String filename);
}
