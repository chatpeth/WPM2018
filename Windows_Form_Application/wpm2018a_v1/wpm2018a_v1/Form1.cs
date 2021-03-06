﻿using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using uPLibrary.Networking.M2Mqtt;
using uPLibrary.Networking.M2Mqtt.Messages;
using wpm2018a_v1.Properties;
using System.IO;

//using System.Net;

namespace wpm2018a_v1
{
    public partial class Form1 : Form
    {
        private MqttClient _Client;
        private String my_broker = "test.mosquitto.org";
        private String NID_str;
        private Node node = new Node();
        private Zone zone = new Zone();
        private Int32 zone_lenght = 0;
        private byte connection_status = 1;
        private Node[] bind_node = new Node[4];
        
        public Form1()
        {
            InitializeComponent();
            //_Client = new MqttClient(IPAddress.Parse("192.168.100.11"));
            try
            {
                _Client = new MqttClient(my_broker);
            }
            catch
            {
                MessageBox.Show("Check your internet connection!", "WPM2018");
                System.Environment.Exit(0);
            }
            
            //byte connection_result = _Client.Connect(Guid.NewGuid().ToString());
            //_Client.Subscribe(new[] { "fff1/pa" }, new byte[] { MqttMsgBase.QOS_LEVEL_EXACTLY_ONCE });
            _Client.ConnectionClosed += _Client_ConnectionClosed;
            connection_status = _Client.Connect(Guid.NewGuid().ToString());
            _Client.Subscribe(new[] { "505atk"}, new byte[] { MqttMsgBase.QOS_LEVEL_EXACTLY_ONCE });
            _Client.MqttMsgPublishReceived += _Client_MqttMsgPublishReceived;
            for(Int32 i = 0; i < 4; i++)
            {
                bind_node[i] = new Node();
            }

        }
        public class Node
        {
            public string id { get; set; }
            public float pa { get; set; }
            public float pb { get; set; }
            public float pc { get; set; }
            public float pt { get; set; }
            public float pavg { get; set; }
            public float pavgmin { get; set; }
            public float ea { get; set; }
            public float eb { get; set; }
            public float ec { get; set; }
            public float et { get; set; }
            public float peaka { get; set; }
            public float peakb { get; set; }
            public float peakc { get; set; }
            public float peakt { get; set; }

        }
        public class Zone
        {
            public string ZID { get; set; }
            public Node[] Node = new Node[10];
            public float sum_p { get; set; }
            public float sum_e { get; set; }

            
        }
        private void _Client_ConnectionClosed(object sender, EventArgs e)
        {
            try
            {
                connection_status = _Client.Connect(Guid.NewGuid().ToString());
                if (connection_status == 0)
                {
                    foreach (ComboboxItem topic in Device_lst.Items)
                    {
                        string sub_topic = topic.NID.ToString();
                        //MessageBox.Show(sub_topic);
                        _Client.Subscribe(new[] { sub_topic + "/#" }, new byte[] { MqttMsgBase.QOS_LEVEL_EXACTLY_ONCE });
                    }
                    MessageBox.Show("Connection restored.");
                }
            }
            catch
            {
                    
            }
            
            
        }
        public void Refresh_bind(int bind_id)
        {
            if(bind_id == 0)
            {
                Bind1_pa_data.Text = bind_node[bind_id].pa.ToString();
                Bind1_pb_data.Text = bind_node[bind_id].pb.ToString();
                Bind1_pc_data.Text = bind_node[bind_id].pc.ToString();
                Bind1_pt_data.Text = bind_node[bind_id].pt.ToString();

                Bind1_pavg_data.Text = bind_node[bind_id].pavg.ToString();
                Bind1_pavgmin_data.Text = bind_node[bind_id].pavgmin.ToString();

                Bind1_ea_data.Text = bind_node[bind_id].ea.ToString();
                Bind1_eb_data.Text = bind_node[bind_id].eb.ToString();
                Bind1_ec_data.Text = bind_node[bind_id].ec.ToString();
                Bind1_et_data.Text = bind_node[bind_id].et.ToString();

                Bind1_peaka_data.Text = bind_node[bind_id].peaka.ToString();
                Bind1_peakb_data.Text = bind_node[bind_id].peakb.ToString();
                Bind1_peakc_data.Text = bind_node[bind_id].peakc.ToString();
                Bind1_peakt_data.Text = bind_node[bind_id].peakt.ToString();
                float limit = 0;
                try
                {
                    limit = Convert.ToSingle(Bind1_limit_txt.Text);
                    
                }
                catch
                {
                    //MessageBox.Show("Please enter number.", "Warning!");
                    Bind1_limit_txt.Text = "10000";
                    return;
                }
                if(bind_node[bind_id].pt > limit)
                {
                    Bind1_pt_data.ForeColor = System.Drawing.Color.Red;
                    
                }
                else
                {
                    Bind1_pt_data.ForeColor = System.Drawing.Color.Black;
                }
            }
            else if (bind_id == 1)
            {
                Bind2_pa_data.Text = bind_node[bind_id].pa.ToString();
                Bind2_pb_data.Text = bind_node[bind_id].pb.ToString();
                Bind2_pc_data.Text = bind_node[bind_id].pc.ToString();
                Bind2_pt_data.Text = bind_node[bind_id].pt.ToString();

                Bind2_pavg_data.Text = bind_node[bind_id].pavg.ToString();
                Bind2_pavgmin_data.Text = bind_node[bind_id].pavgmin.ToString();

                Bind2_ea_data.Text = bind_node[bind_id].ea.ToString();
                Bind2_eb_data.Text = bind_node[bind_id].eb.ToString();
                Bind2_ec_data.Text = bind_node[bind_id].ec.ToString();
                Bind2_et_data.Text = bind_node[bind_id].et.ToString();

                Bind2_peaka_data.Text = bind_node[bind_id].peaka.ToString();
                Bind2_peakb_data.Text = bind_node[bind_id].peakb.ToString();
                Bind2_peakc_data.Text = bind_node[bind_id].peakc.ToString();
                Bind2_peakt_data.Text = bind_node[bind_id].peakt.ToString();
                float limit = 0;
                try
                {
                    limit = Convert.ToSingle(Bind2_limit_txt.Text);
                }
                catch
                {
                    //MessageBox.Show("Please enter number.", "Warning!");
                    Bind1_limit_txt.Text = "10000";
                    return;
                }
                if (bind_node[bind_id].pt > limit)
                {
                    Bind2_pt_data.ForeColor = System.Drawing.Color.Red;
                }
                else
                {
                    Bind2_pt_data.ForeColor = System.Drawing.Color.Black;
                }
            }
            else if (bind_id == 2)
            {
                Bind3_pa_data.Text = bind_node[bind_id].pa.ToString();
                Bind3_pb_data.Text = bind_node[bind_id].pb.ToString();
                Bind3_pc_data.Text = bind_node[bind_id].pc.ToString();
                Bind3_pt_data.Text = bind_node[bind_id].pt.ToString();

                Bind3_pavg_data.Text = bind_node[bind_id].pavg.ToString();
                Bind3_pavgmin_data.Text = bind_node[bind_id].pavgmin.ToString();

                Bind3_ea_data.Text = bind_node[bind_id].ea.ToString();
                Bind3_eb_data.Text = bind_node[bind_id].eb.ToString();
                Bind3_ec_data.Text = bind_node[bind_id].ec.ToString();
                Bind3_et_data.Text = bind_node[bind_id].et.ToString();

                Bind3_peaka_data.Text = bind_node[bind_id].peaka.ToString();
                Bind3_peakb_data.Text = bind_node[bind_id].peakb.ToString();
                Bind3_peakc_data.Text = bind_node[bind_id].peakc.ToString();
                Bind3_peakt_data.Text = bind_node[bind_id].peakt.ToString();
                float limit = 0;
                try
                {
                    limit = Convert.ToSingle(Bind3_limit_txt.Text);
                }
                catch
                {
                    //MessageBox.Show("Please enter number.", "Warning!");
                    Bind1_limit_txt.Text = "10000";
                    return;
                }
                if (bind_node[bind_id].pt > limit)
                {
                    Bind3_pt_data.ForeColor = System.Drawing.Color.Red;
                }
                else
                {
                    Bind3_pt_data.ForeColor = System.Drawing.Color.Black;
                }
            }
        }
        public void Check_msg(String topic, String payload, String bind_name, int bind_id)
        {
            String[] topic_level = topic.Split('/');
            float value = Convert.ToSingle(payload);
            bind_node[bind_id].id = topic_level[0];

            if (topic_level[0] == bind_name)
            {
                //System.Console.WriteLine(bind_name);
                if (topic_level[1] == "p")
                {
                    if (topic_level[2] == "a")
                    {
                        bind_node[bind_id].pa = value;
                    }
                    else if (topic_level[2] == "b")
                    {
                        bind_node[bind_id].pb = value;
                    }
                    else if (topic_level[2] == "c")
                    {
                        bind_node[bind_id].pc = value;
                    }
                    else if (topic_level[2] == "3phase")
                    {
                        bind_node[bind_id].pt = value;
                    }
                }
                else if (topic_level[1] == "pavg")
                {
                    if (topic_level[2] == "3phase")
                    {
                        bind_node[bind_id].pavg = value;
                    }
                    else if (topic_level[2] == "min")
                    {
                        bind_node[bind_id].pavgmin = value;
                    }
                }
                else if (topic_level[1] == "e")
                {
                    if (topic_level[2] == "a")
                    {
                        bind_node[bind_id].ea = value;
                    }
                    else if (topic_level[2] == "b")
                    {
                        bind_node[bind_id].eb = value;
                    }
                    else if (topic_level[2] == "c")
                    {
                        bind_node[bind_id].ec = value;
                    }
                    else if (topic_level[2] == "3phase")
                    {
                        bind_node[bind_id].et = value;
                    }
                }
                else if (topic_level[1] == "peak")
                {
                    if (topic_level[2] == "a")
                    {
                        bind_node[bind_id].peaka = value;
                    }
                    else if (topic_level[2] == "b")
                    {
                        bind_node[bind_id].peakb = value;
                    }
                    else if (topic_level[2] == "c")
                    {
                        bind_node[bind_id].peakc = value;
                    }
                    else if (topic_level[2] == "3phase")
                    {
                        bind_node[bind_id].peakt = value;
                    }
                }
                Refresh_bind(bind_id);

            }

        }
        private void _Client_MqttMsgPublishReceived(object sender, MqttMsgPublishEventArgs e)
        {
            BeginInvoke(new Action(() =>
            {
                
                String payload = Encoding.UTF8.GetString(e.Message);
                String topic = e.Topic;
                float value = 0;
                Payload_lbl.Text = payload;
                Topic_lbl.Text = topic;
                String[] topic_level = topic.Split('/');

                if (topic_level[0] == Username_txt.Text)
                {
                                      
                    if (payload == "$2loginfailednvxdjkg834ivjdgru9087")
                    {
                        MessageBox.Show("Please check your password");
                    }
                    else
                    {
                        try
                        {
                            String[] myurl = payload.Split(',');
                            foreach(string urlopen in myurl)
                            {
                           
                                
                                System.Diagnostics.Process.Start(urlopen);
                            }
                            
                            
                        }
                        catch
                        {
                            //MessageBox.Show("ERR");
                        }
                        
                    }
                    return;
                }
                try
                {
                    value = Convert.ToSingle(payload);
                }
                catch
                {

                }
                
                node.id = topic_level[0];


                if (topic_level[0] == NID_str)
                {
                    if (topic_level[1] == "p")
                    {
                        if (topic_level[2] == "a")
                        {
                            PA_data.Text = payload;
                            node.pa = value;
                        }
                        else if (topic_level[2] == "b")
                        {
                            PB_data.Text = payload;
                            node.pb = value;
                        }
                        else if (topic_level[2] == "c")
                        {
                            PC_data.Text = payload;
                            node.pc = value;
                        }
                        else if (topic_level[2] == "3phase")
                        {
                            PT_data.Text = payload;
                            node.pt = value;
                        }
                    }
                    else if (topic_level[1] == "pavg")
                    {
                        if (topic_level[2] == "3phase")
                        {
                            Pavg_data.Text = payload;
                            node.pavg = value;
                        }
                        else if (topic_level[2] == "min")
                        {
                            Pavg_min_data.Text = payload;
                            node.pavgmin = value;
                        }
                    }
                    else if (topic_level[1] == "e")
                    {
                        if (topic_level[2] == "a")
                        {
                            EA_data.Text = payload;
                            node.ea = value;
                        }
                        else if (topic_level[2] == "b")
                        {
                            EB_data.Text = payload;
                            node.eb = value;
                        }
                        else if (topic_level[2] == "c")
                        {
                            EC_data.Text = payload;
                            node.ec = value;
                        }
                        else if (topic_level[2] == "3phase")
                        {
                            ET_data.Text = payload;
                            node.et = value;
                        }
                    }
                    else if (topic_level[1] == "peak")
                    {
                        if (topic_level[2] == "a")
                        {
                            PeakA_data.Text = payload;
                            node.peaka = value;
                        }
                        else if (topic_level[2] == "b")
                        {
                            PeakB_data.Text = payload;
                            node.peakb = value;
                        }
                        else if (topic_level[2] == "c")
                        {
                            PeakC_data.Text = payload;
                            node.peakc = value;
                        }
                        else if (topic_level[2] == "3phase")
                        {
                            PeakT_data.Text = payload;
                            node.peakt = value;
                        }
                    }

                }
                
               for(Int32 i = 0; i < zone_lenght; i ++)
                {
                    if(topic_level[0] == zone.Node[i].id)
                    {
                        if (topic_level[1] == "p")
                        {
                            
                            if (topic_level[2] == "3phase")
                            {
                                zone.Node[i].pt = value;
                                //MessageBox.Show(value.ToString());
                                
                            }
                        }

                        else if (topic_level[1] == "e")
                        {
                            
                            if (topic_level[2] == "3phase")
                            {
                                zone.Node[i].et = value;
                            }
                        }                       
                    }
                }

                float sum_p = 0, sum_e = 0;
                for(Int32 i = 0; i < zone_lenght; i++)
                {
                    sum_p = sum_p + zone.Node[i].pt;
                    sum_e = sum_e + zone.Node[i].et;
                }
                P_data.Text = sum_p.ToString();
                E_data.Text = sum_e.ToString();

                if(Bind1_txt.Text != "")
                {
                    Check_msg(topic, payload, Bind1_txt.Text, 0);
                }
                if (Bind2_txt.Text != "")
                {
                    Check_msg(topic, payload, Bind2_txt.Text, 1);
                }
                if (Bind2_txt.Text != "")
                {
                    Check_msg(topic, payload, Bind3_txt.Text, 2);
                }



            }));

            
            

        }

        private void Form1_Load(object sender, EventArgs e)
        {
            
            NID_textbox.Text = Settings.Default["NID"].ToString();
            Description_txt.Text = Settings.Default["Location"].ToString();
            Bind1_txt.Text = Settings.Default["Bind1"].ToString();
            Bind2_txt.Text = Settings.Default["Bind2"].ToString();
            Bind3_txt.Text = Settings.Default["Bind3"].ToString();
            Bind1_limit_txt.Text = Settings.Default["Overload1"].ToString();
            Bind2_limit_txt.Text = Settings.Default["Overload2"].ToString();
            Bind3_limit_txt.Text = Settings.Default["Overload3"].ToString();
            Username_txt.Text = Settings.Default["Uname"].ToString();
            string tmp_path = Directory.GetCurrentDirectory();
            tmp_path = tmp_path + "\\devices.txt";
            
            try
            {
                string[] lines = System.IO.File.ReadAllLines(tmp_path);
                foreach (string tmp_item in lines)
                {
                    
                    if(tmp_item != "")
                    {
                        //Console.WriteLine(tmp_item);
                        string[] add_item = tmp_item.Split('@');
                        ComboboxItem item = new ComboboxItem();
                        item.NID = add_item[0];
                        item.Description = add_item[1];
                        Device_lst.SelectedIndex = Device_lst.Items.Add(item);
                        NID_textbox.Text = item.NID.ToString();
                        Description_txt.Text = item.Description.ToString();
                        Connect_btn_Click(sender, e);
                    }
                    
                }
                
            }
            catch(Exception ex)
            {
                Console.WriteLine(ex.Message);
            }
            tmp_path = tmp_path.Replace("device", "zone");
            try
            {
                string[] lines = System.IO.File.ReadAllLines(tmp_path);
                foreach (string tmp_item in lines)
                {

                    if (tmp_item != "")
                    {
                        Console.WriteLine(tmp_item);
                        string[] add_item = tmp_item.Split('@');
                        ZoneItem item = new ZoneItem();
                        item.ZID = add_item[0];
                        item.List = add_item[1];
                        Zone_lst.SelectedIndex = Zone_lst.Items.Add(item);
                        Zone_txt.Text = item.ZID.ToString();
                        List_txt.Text = item.List.ToString();
                        zone_connect_btn_Click(sender, e);
                    }

                }

            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.Message);
            }


            Console.WriteLine("end");
        }

        private void checkBox1_CheckedChanged(object sender, EventArgs e)
        {
            _Client.Publish("led", new[] { checkBox1.Checked ? (byte)1 : (byte)0 });
        }

        private void panel1_Paint(object sender, PaintEventArgs e)
        {

        }

        private void label1_Click(object sender, EventArgs e)
        {
            Payload_lbl.Text = "Hello";
        }

        private void label2_Click(object sender, EventArgs e)
        {

        }

        private void label3_Click(object sender, EventArgs e)
        {

        }

        private void label3_Click_1(object sender, EventArgs e)
        {

        }

        private void Connect_btn_Click(object sender, EventArgs e)
        {
            
            //String report_msg; 
            NID_str = NID_textbox.Text;

            
            _Client.Subscribe(new[] { NID_str + "/#" }, new byte[] { MqttMsgBase.QOS_LEVEL_EXACTLY_ONCE });
            
           if(connection_status == 0)
            {
                //report_msg = " conected";
            }
           else
            {
                //report_msg = " not connect ERR:0x01";
            }
            //MessageBox.Show(NID_str + report_msg, "Connection status");

            Current_node_lbl.Text = NID_str;

            node.pa = 0;
            node.pb = 0;
            node.pc = 0;
            node.pt = 0;
            node.pavg = 0;
            node.pavgmin = 0;
            node.ea = 0;
            node.eb = 0;
            node.ec = 0;
            node.et = 0;
            node.peaka = 0;
            node.peakb = 0;
            node.peakc = 0;
            node.peakt = 0;

            PA_data.Text = "NaN";
            PB_data.Text = "NaN";
            PC_data.Text = "NaN";
            PT_data.Text = "NaN";
            Pavg_data.Text = "NaN";
            Pavg_min_data.Text = "NaN";
            EA_data.Text = "NaN";
            EB_data.Text = "NaN";
            EC_data.Text = "NaN";
            ET_data.Text = "NaN";
            PeakA_data.Text = "NaN";
            PeakB_data.Text = "NaN";
            PeakC_data.Text = "NaN";
            PeakT_data.Text = "NaN";
        }


        public class ComboboxItem
        {
            public string NID { get; set; }
            public object Description { get; set; }

            public override string ToString()
            {
                return NID + "/" + Description;
            }
        }

        private void Add_btn_Click(object sender, EventArgs e)
        {
            ComboboxItem item = new ComboboxItem();
            item.NID = NID_textbox.Text;
            item.Description = Description_txt.Text;
            Device_lst.SelectedIndex = Device_lst.Items.Add(item);
            NID_textbox.Select();
            //MessageBox.Show((Device_lst.SelectedItem as ComboboxItem).Description.ToString());
            Connect_btn_Click(sender, e);
        }

        private void Remove_btn_Click(object sender, EventArgs e)
        {
            Device_lst.Items.Remove(Device_lst.SelectedItem);           
        }

        public void Device_lst_SelectedIndexChanged(object sender, EventArgs e)
        {
            NID_textbox.Text = (Device_lst.SelectedItem as ComboboxItem).NID.ToString();
            if (auto_fill_chk.Checked)
            {         
                Description_txt.Text = (Device_lst.SelectedItem as ComboboxItem).Description.ToString();
                if (List_txt.Text == "")
                {
                    List_txt.Text = NID_textbox.Text;
                }
                else
                {
                    List_txt.Text = List_txt.Text + "," + NID_textbox.Text;
                }
            }

            


        }

        private void splitContainer1_Panel1_Paint(object sender, PaintEventArgs e)
        {

        }

        public class ZoneItem
        {
            public string ZID { get; set; }
            public object List { get; set; }

            public override string ToString()
            {
                return ZID + "/" + List;
            }
        }
        private void Add_zone_btn_Click(object sender, EventArgs e)
        {
            ZoneItem item = new ZoneItem();
            item.ZID = Zone_txt.Text;
            item.List = List_txt.Text;
            Zone_lst.SelectedIndex = Zone_lst.Items.Add(item);
            List_txt.Text = "";
            Zone_txt.Select();

        }

        private void Remove_zone_btn_Click(object sender, EventArgs e)
        {
            Zone_lst.Items.Remove(Zone_lst.SelectedItem);
            
        }

        private void Zone_lbl_Click(object sender, EventArgs e)
        {

        }

        private void zone_connect_btn_Click(object sender, EventArgs e)
        {
            String zone_str;
            
            
            try
            {
                zone_str = Zone_lst.SelectedItem.ToString();

            }
            catch
            {
                MessageBox.Show("Please add new zone");
                return;
            }
            
            String zone_name = zone_str.Split('/')[0];

            Zone_id_lbl.Text = zone_name;

            zone_str = zone_str.Split('/')[1];
            zone_str = zone_str.Replace(" ", string.Empty);
            String[] zone_list = zone_str.Split(',');
            foreach(String node_id in zone_list)
            {
                //System.Console.WriteLine(node_id);
                
                _Client.Subscribe(new[] { node_id + "/#" }, new byte[] { MqttMsgBase.QOS_LEVEL_EXACTLY_ONCE });
            }
            zone_lenght = zone_list.Length;
            for(int i = 0; i < zone_lenght; i++)
            {
                //System.Console.WriteLine(i);
                zone.Node[i] = new Node();
                zone.Node[i].id = zone_list[i];
                //System.Console.WriteLine(zone.Node[i].id);
            }
        }

        private void splitContainer1_Panel2_Paint(object sender, PaintEventArgs e)
        {

        }

        private void splitContainer2_Panel2_Paint(object sender, PaintEventArgs e)
        {

        }

        public void Clear_old_bind(Int32 bind_id)
        {
            bind_node[bind_id].pa = 0;
            bind_node[bind_id].pb = 0;
            bind_node[bind_id].pc = 0;
            bind_node[bind_id].pt = 0;
            bind_node[bind_id].pavg = 0;
            bind_node[bind_id].pavgmin = 0;
            bind_node[bind_id].ea = 0;
            bind_node[bind_id].eb = 0;
            bind_node[bind_id].ec = 0;
            bind_node[bind_id].et = 0;
            bind_node[bind_id].peaka = 0;
            bind_node[bind_id].peakb = 0;
            bind_node[bind_id].peakc = 0;
            bind_node[bind_id].peakt = 0;

        }

        private void Bind2_btn_Click(object sender, EventArgs e)
        {
            Clear_old_bind(0);
            Clear_old_bind(1);
            Clear_old_bind(2);
        }


        private void Reconnect_btn_Click(object sender, EventArgs e)
        {
            try
            {
                _Client.Connect(DateTime.Now.ToUniversalTime().ToString());
                MessageBox.Show(DateTime.Now.ToUniversalTime().ToString());
                foreach(ComboboxItem topic in Device_lst.Items)
                {
                    string sub_topic = topic.NID.ToString();
                    //MessageBox.Show(sub_topic);
                    _Client.Subscribe(new[] { sub_topic + "/#" }, new byte[] { MqttMsgBase.QOS_LEVEL_EXACTLY_ONCE });
                }

            }
            catch
            {
                return;
            }


        }

        private void exitToolStripMenuItem_Click(object sender, EventArgs e)
        {
            System.Environment.Exit(0);
        }

        private void saveToolStripMenuItem_Click(object sender, EventArgs e)
        {
            
            Settings.Default["NID"] = NID_textbox.Text;
            Settings.Default["Location"] = Description_txt.Text;
            Settings.Default["Bind1"] = Bind1_txt.Text;
            Settings.Default["Bind2"] = Bind2_txt.Text;
            Settings.Default["Bind3"] = Bind3_txt.Text;
            Settings.Default["Overload1"] = Bind1_limit_txt.Text;
            Settings.Default["Overload2"] = Bind2_limit_txt.Text;
            Settings.Default["Overload3"] = Bind3_limit_txt.Text;
            Settings.Default["Uname"] = Username_txt.Text;


            string[] lines = new string[50];
            int i = 0;
            foreach(ComboboxItem tmp_item in Device_lst.Items)
            {
                
                lines[i] = tmp_item.NID.ToString() + "@" +tmp_item.Description.ToString();
                i = i + 1;
                
            }
            string tmp_path = Directory.GetCurrentDirectory();          
            tmp_path = tmp_path + "\\devices.txt";
            try
            {
                File.WriteAllText(tmp_path, "");
                File.WriteAllLines(tmp_path, lines);
            }
            catch(Exception ex)
            {
                MessageBox.Show(ex.Message, "Error");
            }

            i = 0;
            string[] tmp_zone_list = new string[50];
            foreach(ZoneItem tmp_zone_item in Zone_lst.Items)
            {
                tmp_zone_list[i] = tmp_zone_item.ZID.ToString() + "@" + tmp_zone_item.List.ToString();
                i = i + 1;
            }
            tmp_path = tmp_path.Replace("device", "zone");
            try
            {
                File.WriteAllText(tmp_path, "");
                File.WriteAllLines(tmp_path, tmp_zone_list);
            }
            catch (Exception ex)
            {
                MessageBox.Show(ex.Message, "Error");
            }
            Settings.Default.Save();
            MessageBox.Show("Saved", "Inform");
        }

        private void menuStrip1_ItemClicked(object sender, ToolStripItemClickedEventArgs e)
        {

        }

        private void dataLogToolStripMenuItem_Click(object sender, EventArgs e)
        {
            System.Diagnostics.Process.Start("https://drive.google.com/drive/shared-with-me");
        }

        private void netpieToolStripMenuItem_Click(object sender, EventArgs e)
        {
            System.Diagnostics.Process.Start("https://netpie.io/freeboard/ioteep");
        }

        private void Login_btn_Click(object sender, EventArgs e)
        {
            String username_str = Username_txt.Text;
            Login_btn.Enabled = false;
            if(username_str != "")
            {
                byte[] bytes = Encoding.ASCII.GetBytes("getlog");
                _Client.Publish(username_str, bytes);
                _Client.Subscribe(new[] { username_str + "/#" }, new byte[] { MqttMsgBase.QOS_LEVEL_EXACTLY_ONCE });
            }
            

            
        }

        private void Username_txt_TextChanged(object sender, EventArgs e)
        {
            Login_btn.Enabled = true;
        }

        private void userManualToolStripMenuItem_Click(object sender, EventArgs e)
        {
            System.Diagnostics.Process.Start("https://drive.google.com/file/d/1NLPPlMqJMzbN1LHAUj85MlvTWcEXkHsJ/view?usp=sharing");
        }
    }
}
