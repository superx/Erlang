import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

public class money {

	public ArrayList<Customer> customers;
	public ArrayList<Bank> banks;
	Output output;
	
	class Customer extends Thread {
		String name;
		Integer money;
		Integer money2;
		public ArrayList<Bank> cbanks;
		public String resp;
		
		public Customer(String name, Integer money) {
			this.name = name;
			this.money = money;
			this.money2 = money;
			cbanks = new ArrayList<money.Bank>();
			synchronized (banks) {
				for(Bank b : banks) {
					cbanks.add(b);
				}
			}
			resp = "";
		}
		
		@Override
		public void run() {
			Random r = new Random(System.currentTimeMillis());
			try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			while(true) {
				try {
					int sleeptime = r.nextInt(90) + 11;
					Thread.sleep(sleeptime);
					if(this.money == 0) {
						String message = name + " has reached the objective of " + money2 + " dollar(s). Woo Hoo!";
						synchronized (output.data) {
							output.data.add(message);
						}
						break;
					} 
					
					if(this.cbanks.size() == 0) {
						String message = name + " was only able to borrow " + (money2 - money) + " dollar(s). Boo Hoo!";
						synchronized (output.data) {
							output.data.add(message);
						}
						break;
					}
					
					Integer loan = r.nextInt(50) + 1;
					if(loan > this.money) {
						loan = this.money;
					}
					
					int tmp = 0;
					if(this.cbanks.size() > 1) {
						tmp = r.nextInt(this.cbanks.size());
					}
					
					if(cbanks.get(tmp) != null) {
						synchronized (cbanks.get(tmp).requests) {
							this.cbanks.get(tmp).requests.put(name, loan);
						}
						synchronized (output.data) {
							String msg = this.name + " requests a loan of " + loan + " dollar(s) from " + this.cbanks.get(tmp).name;
							output.data.add(msg);
						}
					}
					String response = null;
					
					while(true) {
						synchronized (this.resp) {
							if(this.resp.length() > 2) {
								response = resp;
								resp = "";
								break;
							}
						}
						Thread.sleep(10);
					}
					
					if(response != null) {
						if(response.equals("APP")) {
							this.money -= loan;
						}
						else {
							this.cbanks.remove(tmp);
						}
					}
					
				} 
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	class Bank extends Thread {
		String name;
		int money;
		
		public HashMap<String, Integer> requests;
		
		public Bank(String name, int money) {
			this.name = name;
			this.money = money;
			requests = new HashMap<String, Integer>();
		}
		
		@Override
		public void run() {
			int iter = 0;
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			while(true) {
				try {
					iter++;
					Thread.sleep(10);
					if(this.requests.size() > 0) {
						String custname = "";
						Integer custamt = 0;
						
						synchronized (requests) {
							Set<String> keys = requests.keySet();
							custname = keys.iterator().next();
							custamt = requests.get(custname);
							requests.remove(custname);
						}
						
						String message = "";
						
						if(custamt <= this.money) {
							// Approve
							message = name + " approves a loan of " + custamt + " dollar(s) from " + custname;
							this.money -= custamt;
							synchronized (customers) {
								for(Customer c : customers) {
									if(c.name.equals(custname)) {
										synchronized (c.resp) {
											c.resp = "APP";
										}
									}
								}
							}
						}
						else {
							// Denies
							message = name + " denies a loan of " + custamt + " dollar(s) from " + custname;
							for(Customer c : customers) {
								if(c.name.equals(custname)) {
									synchronized (c.resp) {
										c.resp = "DEN";
									}
								}
							}
						}
						
						if(message.length() > 1) {
							iter = 0;
							synchronized (output.data) {
								output.data.add(message);
							}
						}
					}
					
					if(iter >= 100) {
						if(this.money > 0) {
							String message = name + " has " + money + " dollar(s) remaining.";
							synchronized (output.data) {
								output.data.add(message);
							}
						}
						break;
					}
				}
				catch(Exception e) {
					
				}
			}
		}
	}
	
	class Output extends Thread {
		ArrayList<String> data = new ArrayList<String>();
		
		@Override
		public void run() {
			int iter = 0;
			while(true) {
				if(data.size() > 0) {
					iter = 0;
					synchronized (data) {
						System.out.println(data.get(0));
						data.remove(0);
					}
				}
				
				if(iter >= 500) {
					break;
				}
				
				try {
					Thread.sleep(10);
					iter++;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public void startBank(String[] tmpData) {
		Bank b = new Bank(tmpData[0], Integer.parseInt(tmpData[1]));
		b.start();
		this.banks.add(b);
	}
	
	public void startCustomer(String[] tmpData) {
		Customer c = new Customer(tmpData[0], Integer.parseInt(tmpData[1]));
		c.start();
		this.customers.add(c);
	}
	
	public void startOutput() {
		output = new Output();
		output.start();
	}
	
	public static void main(String[] args) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File("banks.txt")));
			BufferedReader br2 = new BufferedReader(new FileReader(new File("customers.txt")));
			money m = new money();
			m.banks = new ArrayList<money.Bank>();
			m.customers = new ArrayList<money.Customer>();
			String data = null;
			
			while ((data = br.readLine()) != null) {
				data = data.substring(1,data.length() - 2);
				String[] tmpData = data.split(",");
				m.startBank(tmpData);
			}
			
			System.out.println("** Customers and loan objectives **");
			
			while ((data = br2.readLine()) != null) {
				data = data.substring(1,data.length() - 2);
				String[] tmpData = data.split(",");
				System.out.println(tmpData[0] + ": " + tmpData[1]);
				m.startCustomer(tmpData);
			}
			
			System.out.println("\n** Banks and financial resources **\n");
			br = new BufferedReader(new FileReader(new File("banks.txt")));
			while ((data = br.readLine()) != null) {
				data = data.substring(1,data.length() - 2);
				String[] tmpData = data.split(",");
				System.out.println(tmpData[0] + ": " + tmpData[1]);
			}
			
			m.startOutput();
		}
		catch(Exception e) {
			System.out.println(e);
		}
	}

}
