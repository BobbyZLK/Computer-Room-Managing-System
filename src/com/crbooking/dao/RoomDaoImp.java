package com.crbooking.dao;

import com.crbooking.bean.Room;
import com.crbooking.bean.query.RoomQuery;

import jakarta.annotation.Resource;

import java.time.format.DateTimeFormatter;
import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

@Repository("roomDao")
public class RoomDaoImp implements RoomDao{  
	@Resource
	private SessionFactory sessionFactory;
	
	private String format="yyyy-MM-dd HH:mm:ss";
    public DateTimeFormatter dtf=DateTimeFormatter.ofPattern(format);
	
	private Session getSession() {return sessionFactory.getCurrentSession();}
    
	@Override
	public Integer saveRoom(Room room) throws Exception{
    	if(room.getId()==null) {
    		this.getSession().save(room);}
    	else {
    		this.getSession().update(room);
    	}
        return room.getId();
	}
	
	
	@Override
	public Boolean deleteRoom(Integer id) throws Exception{
		Session session=this.getSession();
		Room room=(Room)session.get(Room.class, id);
		session.delete(room);
		return true;	
	}
	
	@Override
	public List<Room> queryRoom(RoomQuery roomquery) throws Exception {  
		
		//如果重新去看一遍就会发现hibernate三种查询方式应该是平等的，而且hql只能在5select和where中用子查询，所以别固执于hql虽然它高度封装且用一级缓存，它做不到的让sql来
		     StringBuffer sql=new StringBuffer();
             if(roomquery.getJoinRecordStart()!=null & roomquery.getJoinRecordEnd()!=null) {
            	 sql.append("SELECT * FROM room AS r LEFT JOIN "
            	 		+ "(SELECT roomId, MIN(startingTime) AS min FROM bookingrecord "
            	 		+ "WHERE recordCondition=0 AND seatId IS NULL "
            	 		+ "AND startingTime<'"+dtf.format(roomquery.getJoinRecordEnd())+"' AND endingTime>'"+dtf.format(roomquery.getJoinRecordStart())+"' "
            	 		+ "GROUP BY roomId) AS b on r.id=b.roomId WHERE 1=1 ");
             }else {
            	 sql.append("SELECT * FROM room AS r WHERE ");
             }
             
             
             StringBuffer params=new StringBuffer();
             //如果要分情况，以下条件可能需要重复，那就封装起来复用
             if(roomquery.getId()!=null) {
           	  params.append(" AND  r.id='"+ roomquery.getId()+"'"); 
             }
             if(roomquery.getRoomName()!=null) {
           	  params.append(" AND  r.roomName='"+ roomquery.getRoomName()+"'");
             }
             if(roomquery.getRoomCondition()!=null) {
           	  params.append(" AND r.roomCondition='"+roomquery.getRoomCondition()+"'");
             }
             if(StringUtils.isNotBlank(roomquery.getQueryName())) {
           	  params.append(" AND r.roomName LIKE '%"+roomquery.getQueryName()+"%' ");
             }
             
             sql.append(params);
             sql.append(" ORDER BY r.isBanned asc ");
             if(roomquery.getJoinRecordStart()!=null && roomquery.getJoinRecordEnd()!=null) {
            	 Integer start=roomquery.getJoinRecordStart().getHour();
            	 Integer end=roomquery.getJoinRecordEnd().getHour();
            	 sql.append(" ,CASE WHEN r.openTime>"+start+" OR r.closeTime<"+end+" THEN 1 ELSE 0 END  asc"
            	 		+ " , NOT ISNULL(b.min), b.min asc");
             }
              sql.append(" ,r.openTime asc,r.closeTime desc ");
              
              Session session=this.getSession();
              Query query=session.createSQLQuery(sql.toString()).addEntity(Room.class);
             
              if(roomquery.getNowPage()!=null && roomquery.getPageSize()!=null) {
            	  query.setFirstResult((roomquery.getNowPage()-1)*roomquery.getPageSize());
            	  query.setMaxResults(roomquery.getPageSize());
              }
                      
              List<Room>result=query.list();
              return result;
}
	
	
	@Override
	public Integer queryTotalNumber(RoomQuery roomquery)throws Exception{
		StringBuffer hql=new StringBuffer("select count(distinct r.id) from Room r where 1=1 ");
		
		if(roomquery.getId()!=null) {
		  hql.append(" AND  r.id='"+ roomquery.getId()+"'");
         }
         if(roomquery.getRoomName()!=null) {
          hql.append(" AND  r.roomName='"+ roomquery.getRoomName()+"'");
         }
         if(roomquery.getRoomCondition()!=null) {
          hql.append(" AND r.roomCondition='"+roomquery.getRoomCondition()+"'");
         }
         if(StringUtils.isNotBlank(roomquery.getQueryName())) {
          hql.append(" AND r.roomName LIKE '%"+roomquery.getQueryName()+"%' ");
         }
    
		Session session=this.getSession();
		Query query=session.createQuery(hql.toString());
        int i=((Number) query.uniqueResult()).intValue();
        return i;
	}
	
	
}