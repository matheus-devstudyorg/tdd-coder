package com.study.junit.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class Location {
	private User user;
	private List<Movie> movies;
	private Date locationDate;
	private Date devolutionDate;
	private Double price;
}