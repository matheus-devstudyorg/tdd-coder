package com.study.junit.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Location {
	private User user;
	private List<Movie> movies;
	private Date locationDate;
	private Date devolutionDate;
	private Double price;

	public Location(User user, Movie movie, Date locationDate, Date devolutionDate, Double price){
		this.user = user;
		this.movies = List.of(movie);
		this.locationDate = locationDate;
		this.devolutionDate = devolutionDate;
		this.price = price;
	}
}