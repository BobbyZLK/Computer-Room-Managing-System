package com.crbooking.dao;

import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import com.crbooking.bean.*;

import com.crbooking.bean.query.SeatQuery;


@Repository("seatDao")
public class SeatDaoImp implements SeatDao{
	
    @Override
    public Integer saveSeat(Seat seat) throws Exception{
        
    	return action.save(seat);
    
    }
    
    
    @Override
    public Boolean updateSeats(List<Seat> seats)throws Exception{
    	
    	
    }
    
    
    @Override
    public Boolean deleteSeat(Integer i) throws Exception{
		
    	return action.delete(Seat.class, i);
	}

    @Override
    public Boolean deleteSeats(List<Integer> ids) throws Exception{
    	 
    	return action.multiDeletions(ids, "Seat");
    }
    
    @Override
    public List<Seat> querySeat(SeatQuery seatquery)throws Exception{
    	String hql="from Seat WHERE 1=1 ";
        if(seatquery.getId()!=null) {
      	  hql+=" and  id='"+ seatquery.getId()+"'";
        }
        if(seatquery.getSeatName()!=null) {
      	  hql+=" and  seatName='"+ seatquery.getSeatName()+"'";
        }
        if(seatquery.getRoom()!=null) {
        	hql+=" and room=?0 ";
        }
        if(StringUtils.isNotBlank(seatquery.getQueryName())) {
        	hql+=" and seatName LIKE '%"+seatquery.getQueryName()+"%' ";
        }
        
        Session session=getSession.sessionGetter();
        Query query=session.createQuery(hql);
        
        if(seatquery.getPageSize()!=null && seatquery.getNowPage()!=null) {
            query.setFirstResult((seatquery.getNowPage()-1)*seatquery.getPageSize());
            query.setMaxResults(seatquery.getPageSize());
        }
        
        if(seatquery.getRoom()!=null) {
        	query.setParameter(0, seatquery.getRoom());
        }
        List<Seat> results=query.list();
        session.close();
        return results;
    }
    
    @Override
    public Integer queryTotalNumber(SeatQuery seatquery)throws Exception{
    	String hql="select count(*) from Seat where 1=1 ";
    	 if(seatquery.getId()!=null) {
         	  hql+=" and  id='"+ seatquery.getId()+"'";
           }
           if(seatquery.getSeatName()!=null) {
         	  hql+=" and  seatName='"+ seatquery.getSeatName()+"'";
           }
           if(seatquery.getRoom()!=null) {
           	hql+=" and room=?0 ";
           }
           if(StringUtils.isNotBlank(seatquery.getQueryName())) {
           	hql+=" and seatName LIKE '%"+seatquery.getQueryName()+"%' ";
           }
           Session session=getSession.sessionGetter();
           Query query=session.createQuery(hql);
           if(seatquery.getRoom()!=null) {
           	query.setParameter(0, seatquery.getRoom());
           }
           int i=((Number) query.uniqueResult()).intValue();
           session.close();
           return i;
    }
}

